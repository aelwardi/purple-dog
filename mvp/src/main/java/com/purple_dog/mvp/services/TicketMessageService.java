package com.purple_dog.mvp.services;

import com.purple_dog.mvp.dao.PersonRepository;
import com.purple_dog.mvp.dao.SupportTicketRepository;
import com.purple_dog.mvp.dao.TicketMessageRepository;
import com.purple_dog.mvp.dto.TicketMessageCreateDTO;
import com.purple_dog.mvp.dto.TicketMessageDTO;
import com.purple_dog.mvp.entities.Admin;
import com.purple_dog.mvp.entities.Person;
import com.purple_dog.mvp.entities.SupportTicket;
import com.purple_dog.mvp.entities.TicketMessage;
import com.purple_dog.mvp.entities.TicketStatus;
import com.purple_dog.mvp.exceptions.InvalidOperationException;
import com.purple_dog.mvp.exceptions.ResourceNotFoundException;
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
public class TicketMessageService {

    private final TicketMessageRepository messageRepository;
    private final SupportTicketRepository ticketRepository;
    private final PersonRepository personRepository;

    /**
     * Ajouter un message à un ticket
     */
    public TicketMessageDTO addMessage(Long ticketId, Long senderId, TicketMessageCreateDTO dto) {
        log.info("Adding message to ticket {} from user {}", ticketId, senderId);

        SupportTicket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + ticketId));

        Person sender = personRepository.findById(senderId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + senderId));

        // Vérifier que l'utilisateur a le droit d'envoyer un message sur ce ticket
        validateMessagePermission(ticket, sender);

        // Vérifier que le ticket n'est pas fermé
        if (ticket.getStatus() == TicketStatus.CLOSED) {
            throw new InvalidOperationException("Cannot add message to a closed ticket");
        }

        // Déterminer si c'est une réponse du staff
        boolean isStaffReply = sender instanceof Admin;

        // Créer le message
        TicketMessage message = TicketMessage.builder()
                .supportTicket(ticket)
                .sender(sender)
                .content(dto.getContent())
                .isStaffReply(isStaffReply)
                .createdAt(LocalDateTime.now())
                .build();

        message = messageRepository.save(message);

        // Mettre à jour le statut du ticket si nécessaire
        if (isStaffReply && ticket.getStatus() == TicketStatus.OPEN) {
            ticket.setStatus(TicketStatus.IN_PROGRESS);
        } else if (!isStaffReply && ticket.getStatus() == TicketStatus.WAITING_CUSTOMER) {
            ticket.setStatus(TicketStatus.IN_PROGRESS);
        }

        ticket.setUpdatedAt(LocalDateTime.now());
        ticketRepository.save(ticket);

        log.info("Message added successfully to ticket: {}", ticketId);
        return mapToDTO(message);
    }

    /**
     * Récupérer tous les messages d'un ticket
     */
    public List<TicketMessageDTO> getTicketMessages(Long ticketId) {
        log.info("Fetching messages for ticket: {}", ticketId);

        if (!ticketRepository.existsById(ticketId)) {
            throw new ResourceNotFoundException("Ticket not found with id: " + ticketId);
        }

        return messageRepository.findBySupportTicketIdOrderByCreatedAtAsc(ticketId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupérer un message par son ID
     */
    public TicketMessageDTO getMessageById(Long messageId) {
        log.info("Fetching message: {}", messageId);

        TicketMessage message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with id: " + messageId));

        return mapToDTO(message);
    }

    /**
     * Mettre à jour un message
     */
    public TicketMessageDTO updateMessage(Long messageId, Long userId, TicketMessageCreateDTO dto) {
        log.info("Updating message {} by user {}", messageId, userId);

        TicketMessage message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with id: " + messageId));

        // Vérifier que l'utilisateur est bien l'auteur du message
        if (!message.getSender().getId().equals(userId)) {
            throw new InvalidOperationException("You can only edit your own messages");
        }

        // Ne permettre la modification que dans les 15 premières minutes
        if (message.getCreatedAt().plusMinutes(15).isBefore(LocalDateTime.now())) {
            throw new InvalidOperationException("Messages can only be edited within 15 minutes of creation");
        }

        message.setContent(dto.getContent());
        message = messageRepository.save(message);

        // Mettre à jour le timestamp du ticket
        SupportTicket ticket = message.getSupportTicket();
        ticket.setUpdatedAt(LocalDateTime.now());
        ticketRepository.save(ticket);

        log.info("Message updated successfully");
        return mapToDTO(message);
    }

    /**
     * Supprimer un message
     */
    public void deleteMessage(Long messageId, Long userId, boolean isAdmin) {
        log.info("Deleting message {} by user {}", messageId, userId);

        TicketMessage message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with id: " + messageId));

        // Seul l'auteur ou un admin peut supprimer le message
        if (!isAdmin && !message.getSender().getId().equals(userId)) {
            throw new InvalidOperationException("You can only delete your own messages");
        }

        SupportTicket ticket = message.getSupportTicket();
        messageRepository.delete(message);

        // Mettre à jour le timestamp du ticket
        ticket.setUpdatedAt(LocalDateTime.now());
        ticketRepository.save(ticket);

        log.info("Message deleted successfully");
    }

    /**
     * Compter les messages d'un ticket
     */
    public long countTicketMessages(Long ticketId) {
        return messageRepository.countByTicketId(ticketId);
    }

    /**
     * Compter les réponses du staff sur un ticket
     */
    public long countStaffReplies(Long ticketId) {
        return messageRepository.countStaffRepliesByTicketId(ticketId);
    }

    /**
     * Récupérer le dernier message d'un ticket
     */
    public TicketMessageDTO getLastMessage(Long ticketId) {
        TicketMessage message = messageRepository.findLastMessageByTicketId(ticketId);
        return message != null ? mapToDTO(message) : null;
    }

    // Méthodes privées

    private void validateMessagePermission(SupportTicket ticket, Person sender) {
        boolean isTicketOwner = ticket.getUser().getId().equals(sender.getId());
        boolean isAssignedAdmin = ticket.getAssignedAdmin() != null &&
                                  ticket.getAssignedAdmin().getId().equals(sender.getId());
        boolean isAdmin = sender instanceof Admin;

        if (!isTicketOwner && !isAssignedAdmin && !isAdmin) {
            throw new InvalidOperationException("You don't have permission to send messages on this ticket");
        }
    }

    private TicketMessageDTO mapToDTO(TicketMessage message) {
        return TicketMessageDTO.builder()
                .id(message.getId())
                .ticketId(message.getSupportTicket().getId())
                .senderId(message.getSender().getId())
                .senderName(message.getSender().getFirstName() + " " + message.getSender().getLastName())
                .senderEmail(message.getSender().getEmail())
                .content(message.getContent())
                .isStaffReply(message.getIsStaffReply())
                .createdAt(message.getCreatedAt())
                .build();
    }
}

