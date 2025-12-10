package com.purple_dog.mvp.services;

import com.purple_dog.mvp.dao.ConversationRepository;
import com.purple_dog.mvp.dao.MessageRepository;
import com.purple_dog.mvp.dao.PersonRepository;
import com.purple_dog.mvp.dto.ConversationDTO;
import com.purple_dog.mvp.dto.ConversationStartDTO;
import com.purple_dog.mvp.dto.MessageCreateDTO;
import com.purple_dog.mvp.dto.MessageDTO;
import com.purple_dog.mvp.entities.*;
import com.purple_dog.mvp.exceptions.InvalidOperationException;
import com.purple_dog.mvp.exceptions.ResourceNotFoundException;
import com.purple_dog.mvp.utils.MessageContentFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MessagingService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final PersonRepository personRepository;
    private final MessageContentFilter contentFilter;

    /**
     * Démarrer une nouvelle conversation ou récupérer une existante
     */
    public ConversationDTO startOrGetConversation(Long userId, ConversationStartDTO dto) {
        log.info("Starting/getting conversation between user {} and {}", userId, dto.getRecipientId());

        // Vérifier que les utilisateurs existent
        Person user = personRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        Person recipient = personRepository.findById(dto.getRecipientId())
                .orElseThrow(() -> new ResourceNotFoundException("Recipient not found: " + dto.getRecipientId()));

        // Vérifier que ce ne sont pas des admins et que ce sont des types d'utilisateurs autorisés
        if (user instanceof Admin || recipient instanceof Admin) {
            throw new InvalidOperationException("Admins cannot use direct messaging");
        }

        if (userId.equals(dto.getRecipientId())) {
            throw new InvalidOperationException("Cannot start conversation with yourself");
        }

        // Chercher une conversation existante
        Conversation conversation = conversationRepository
                .findConversationBetweenUsers(userId, dto.getRecipientId())
                .orElseGet(() -> {
                    // Créer une nouvelle conversation
                    Conversation newConv = Conversation.builder()
                            .user1(user)
                            .user2(recipient)
                            .user1Archived(false)
                            .user2Archived(false)
                            .createdAt(LocalDateTime.now())
                            .build();
                    return conversationRepository.save(newConv);
                });

        // Envoyer le message initial si fourni
        if (dto.getInitialMessage() != null && !dto.getInitialMessage().trim().isEmpty()) {
            MessageCreateDTO messageDTO = MessageCreateDTO.builder()
                    .content(dto.getInitialMessage())
                    .build();
            sendMessage(conversation.getId(), userId, messageDTO);
        }

        return mapConversationToDTO(conversation, userId);
    }

    /**
     * Récupérer toutes les conversations d'un utilisateur
     */
    public List<ConversationDTO> getUserConversations(Long userId) {
        log.info("Fetching conversations for user: {}", userId);

        if (!personRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found: " + userId);
        }

        return conversationRepository.findActiveConversationsByUserId(userId).stream()
                .map(conv -> mapConversationToDTO(conv, userId))
                .collect(Collectors.toList());
    }

    /**
     * Récupérer une conversation par ID
     */
    public ConversationDTO getConversation(Long conversationId, Long userId) {
        log.info("Fetching conversation {} for user {}", conversationId, userId);

        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found: " + conversationId));

        // Vérifier que l'utilisateur fait partie de la conversation
        if (!isUserInConversation(conversation, userId)) {
            throw new InvalidOperationException("You are not part of this conversation");
        }

        return mapConversationToDTO(conversation, userId);
    }

    /**
     * Envoyer un message dans une conversation
     */
    public MessageDTO sendMessage(Long conversationId, Long senderId, MessageCreateDTO dto) {
        log.info("Sending message in conversation {} from user {}", conversationId, senderId);

        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found: " + conversationId));

        Person sender = personRepository.findById(senderId)
                .orElseThrow(() -> new ResourceNotFoundException("Sender not found: " + senderId));

        // Vérifier que l'utilisateur fait partie de la conversation
        if (!isUserInConversation(conversation, senderId)) {
            throw new InvalidOperationException("You are not part of this conversation");
        }

        // Filtrer le contenu du message
        MessageContentFilter.FilterResult filterResult = contentFilter.filterContent(dto.getContent());

        Message message;

        if (filterResult.shouldBlock()) {
            // Message bloqué - créer un message bloqué
            message = Message.builder()
                    .conversation(conversation)
                    .sender(sender)
                    .content("⚠️ Ce message a été bloqué par le système de modération")
                    .originalContent(dto.getContent())
                    .wasFiltered(true)
                    .filteredWords(filterResult.getFilteredWordsString())
                    .isBlocked(true)
                    .blockReason(filterResult.getBlockReason())
                    .isRead(false)
                    .createdAt(LocalDateTime.now())
                    .build();

            log.warn("Message blocked from user {}: {}", senderId, filterResult.getBlockReason());
        } else {
            // Message autorisé (possiblement filtré)
            message = Message.builder()
                    .conversation(conversation)
                    .sender(sender)
                    .content(filterResult.wasFiltered() ? filterResult.getFilteredContent() : dto.getContent())
                    .originalContent(filterResult.wasFiltered() ? dto.getContent() : null)
                    .wasFiltered(filterResult.wasFiltered())
                    .filteredWords(filterResult.getFilteredWordsString())
                    .isBlocked(false)
                    .isRead(false)
                    .createdAt(LocalDateTime.now())
                    .build();

            if (filterResult.wasFiltered()) {
                log.info("Message filtered from user {}: detected patterns - {}",
                        senderId, filterResult.getFilteredWordsString());
            }
        }

        message = messageRepository.save(message);

        // Mettre à jour le timestamp de la conversation
        conversation.setLastMessageAt(LocalDateTime.now());
        conversationRepository.save(conversation);

        return mapMessageToDTO(message, senderId);
    }

    /**
     * Récupérer tous les messages d'une conversation
     */
    public List<MessageDTO> getConversationMessages(Long conversationId, Long userId) {
        log.info("Fetching messages for conversation {} by user {}", conversationId, userId);

        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found: " + conversationId));

        if (!isUserInConversation(conversation, userId)) {
            throw new InvalidOperationException("You are not part of this conversation");
        }

        // Retourner seulement les messages non bloqués
        return messageRepository.findNonBlockedMessagesByConversationId(conversationId).stream()
                .map(msg -> mapMessageToDTO(msg, userId))
                .collect(Collectors.toList());
    }

    /**
     * Marquer tous les messages d'une conversation comme lus
     */
    public void markConversationAsRead(Long conversationId, Long userId) {
        log.info("Marking conversation {} as read for user {}", conversationId, userId);

        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found: " + conversationId));

        if (!isUserInConversation(conversation, userId)) {
            throw new InvalidOperationException("You are not part of this conversation");
        }

        messageRepository.markAllAsReadInConversation(conversationId, userId);
    }

    /**
     * Archiver une conversation
     */
    public void archiveConversation(Long conversationId, Long userId) {
        log.info("Archiving conversation {} for user {}", conversationId, userId);

        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found: " + conversationId));

        if (!isUserInConversation(conversation, userId)) {
            throw new InvalidOperationException("You are not part of this conversation");
        }

        if (conversation.getUser1().getId().equals(userId)) {
            conversation.setUser1Archived(true);
        } else {
            conversation.setUser2Archived(true);
        }

        conversationRepository.save(conversation);
    }

    /**
     * Désarchiver une conversation
     */
    public void unarchiveConversation(Long conversationId, Long userId) {
        log.info("Unarchiving conversation {} for user {}", conversationId, userId);

        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found: " + conversationId));

        if (!isUserInConversation(conversation, userId)) {
            throw new InvalidOperationException("You are not part of this conversation");
        }

        if (conversation.getUser1().getId().equals(userId)) {
            conversation.setUser1Archived(false);
        } else {
            conversation.setUser2Archived(false);
        }

        conversationRepository.save(conversation);
    }

    /**
     * Compter les messages non lus total de l'utilisateur
     */
    public long countTotalUnreadMessages(Long userId) {
        return messageRepository.countTotalUnreadMessagesByUserId(userId);
    }

    /**
     * Récupérer les messages filtrés (pour les admins)
     */
    public List<MessageDTO> getFilteredMessages() {
        log.info("Fetching filtered messages for admin review");

        return messageRepository.findFilteredMessages().stream()
                .map(msg -> mapMessageToDTO(msg, null))
                .collect(Collectors.toList());
    }

    /**
     * Récupérer les messages bloqués (pour les admins)
     */
    public List<MessageDTO> getBlockedMessages() {
        log.info("Fetching blocked messages for admin review");

        return messageRepository.findBlockedMessages().stream()
                .map(msg -> mapMessageToDTO(msg, null))
                .collect(Collectors.toList());
    }

    // Méthodes privées

    private boolean isUserInConversation(Conversation conversation, Long userId) {
        return conversation.getUser1().getId().equals(userId) ||
               conversation.getUser2().getId().equals(userId);
    }

    private Person getOtherUser(Conversation conversation, Long userId) {
        return conversation.getUser1().getId().equals(userId) ?
               conversation.getUser2() : conversation.getUser1();
    }

    private boolean isArchived(Conversation conversation, Long userId) {
        return conversation.getUser1().getId().equals(userId) ?
               conversation.getUser1Archived() : conversation.getUser2Archived();
    }

    private ConversationDTO mapConversationToDTO(Conversation conversation, Long currentUserId) {
        Person otherUser = getOtherUser(conversation, currentUserId);
        long unreadCount = messageRepository.countUnreadMessagesByConversationAndUser(
                conversation.getId(), currentUserId);

        // Récupérer le dernier message non bloqué
        List<Message> messages = messageRepository.findNonBlockedMessagesByConversationId(conversation.getId());
        Message lastMessage = messages.isEmpty() ? null : messages.get(messages.size() - 1);

        return ConversationDTO.builder()
                .id(conversation.getId())
                .otherUserId(otherUser.getId())
                .otherUserFirstName(otherUser.getFirstName())
                .otherUserRole(otherUser.getRole().name())
                .otherUserProfilePicture(otherUser.getProfilePicture())
                .orderId(conversation.getOrder() != null ? conversation.getOrder().getId() : null)
                .archived(isArchived(conversation, currentUserId))
                .createdAt(conversation.getCreatedAt())
                .lastMessageAt(conversation.getLastMessageAt())
                .lastMessage(lastMessage != null ? mapMessageToDTO(lastMessage, currentUserId) : null)
                .unreadCount(unreadCount)
                .build();
    }

    private MessageDTO mapMessageToDTO(Message message, Long currentUserId) {
        return MessageDTO.builder()
                .id(message.getId())
                .conversationId(message.getConversation().getId())
                .senderId(message.getSender().getId())
                .senderFirstName(message.getSender().getFirstName())
                .senderRole(message.getSender().getRole().name())
                .content(message.getContent())
                .wasFiltered(message.getWasFiltered())
                .isBlocked(message.getIsBlocked())
                .blockReason(message.getBlockReason())
                .isRead(message.getIsRead())
                .readAt(message.getReadAt())
                .createdAt(message.getCreatedAt())
                .isMine(currentUserId != null && message.getSender().getId().equals(currentUserId))
                .build();
    }
}

