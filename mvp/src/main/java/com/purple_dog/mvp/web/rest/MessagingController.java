package com.purple_dog.mvp.web.rest;

import com.purple_dog.mvp.dto.ConversationDTO;
import com.purple_dog.mvp.dto.ConversationStartDTO;
import com.purple_dog.mvp.dto.MessageCreateDTO;
import com.purple_dog.mvp.dto.MessageDTO;
import com.purple_dog.mvp.services.MessagingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Tag(name = "Messaging Management", description = "APIs for managing user messaging and conversations")
@RestController
@RequestMapping("/messaging")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class MessagingController {

    private final MessagingService messagingService;

    /**
     * Démarrer ou récupérer une conversation
     */
    @PostMapping("/conversations/start/{userId}")
    public ResponseEntity<ConversationDTO> startConversation(
            @PathVariable Long userId,
            @Valid @RequestBody ConversationStartDTO dto) {

        log.info("Request to start conversation from user {} to user {}", userId, dto.getRecipientId());
        ConversationDTO conversation = messagingService.startOrGetConversation(userId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(conversation);
    }

    /**
     * Récupérer toutes les conversations d'un utilisateur
     */
    @GetMapping("/conversations/user/{userId}")
    public ResponseEntity<List<ConversationDTO>> getUserConversations(@PathVariable Long userId) {
        log.info("Request to get conversations for user: {}", userId);
        List<ConversationDTO> conversations = messagingService.getUserConversations(userId);
        return ResponseEntity.ok(conversations);
    }

    /**
     * Récupérer une conversation spécifique
     */
    @GetMapping("/conversations/{conversationId}/user/{userId}")
    public ResponseEntity<ConversationDTO> getConversation(
            @PathVariable Long conversationId,
            @PathVariable Long userId) {

        log.info("Request to get conversation {} for user {}", conversationId, userId);
        ConversationDTO conversation = messagingService.getConversation(conversationId, userId);
        return ResponseEntity.ok(conversation);
    }

    /**
     * Envoyer un message dans une conversation
     */
    @PostMapping("/conversations/{conversationId}/messages/user/{userId}")
    public ResponseEntity<MessageDTO> sendMessage(
            @PathVariable Long conversationId,
            @PathVariable Long userId,
            @Valid @RequestBody MessageCreateDTO dto) {

        log.info("Request to send message in conversation {} from user {}", conversationId, userId);
        MessageDTO message = messagingService.sendMessage(conversationId, userId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }

    /**
     * Récupérer tous les messages d'une conversation
     */
    @GetMapping("/conversations/{conversationId}/messages/user/{userId}")
    public ResponseEntity<List<MessageDTO>> getConversationMessages(
            @PathVariable Long conversationId,
            @PathVariable Long userId) {

        log.info("Request to get messages for conversation {} by user {}", conversationId, userId);
        List<MessageDTO> messages = messagingService.getConversationMessages(conversationId, userId);
        return ResponseEntity.ok(messages);
    }

    /**
     * Marquer une conversation comme lue
     */
    @PutMapping("/conversations/{conversationId}/read/user/{userId}")
    public ResponseEntity<Void> markConversationAsRead(
            @PathVariable Long conversationId,
            @PathVariable Long userId) {

        log.info("Request to mark conversation {} as read for user {}", conversationId, userId);
        messagingService.markConversationAsRead(conversationId, userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Archiver une conversation
     */
    @PutMapping("/conversations/{conversationId}/archive/user/{userId}")
    public ResponseEntity<Void> archiveConversation(
            @PathVariable Long conversationId,
            @PathVariable Long userId) {

        log.info("Request to archive conversation {} for user {}", conversationId, userId);
        messagingService.archiveConversation(conversationId, userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Désarchiver une conversation
     */
    @PutMapping("/conversations/{conversationId}/unarchive/user/{userId}")
    public ResponseEntity<Void> unarchiveConversation(
            @PathVariable Long conversationId,
            @PathVariable Long userId) {

        log.info("Request to unarchive conversation {} for user {}", conversationId, userId);
        messagingService.unarchiveConversation(conversationId, userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Compter les messages non lus
     */
    @GetMapping("/user/{userId}/unread/count")
    public ResponseEntity<Long> countUnreadMessages(@PathVariable Long userId) {
        log.info("Request to count unread messages for user: {}", userId);
        long count = messagingService.countTotalUnreadMessages(userId);
        return ResponseEntity.ok(count);
    }

    /**
     * Récupérer les messages filtrés (Admin seulement)
     */
    @GetMapping("/admin/filtered-messages")
    public ResponseEntity<List<MessageDTO>> getFilteredMessages() {
        log.info("Request to get filtered messages for admin review");
        List<MessageDTO> messages = messagingService.getFilteredMessages();
        return ResponseEntity.ok(messages);
    }

    /**
     * Récupérer les messages bloqués (Admin seulement)
     */
    @GetMapping("/admin/blocked-messages")
    public ResponseEntity<List<MessageDTO>> getBlockedMessages() {
        log.info("Request to get blocked messages for admin review");
        List<MessageDTO> messages = messagingService.getBlockedMessages();
        return ResponseEntity.ok(messages);
    }
}
