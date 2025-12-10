package com.purple_dog.mvp.web.rest;

import com.purple_dog.mvp.dto.TicketMessageCreateDTO;
import com.purple_dog.mvp.dto.TicketMessageDTO;
import com.purple_dog.mvp.services.TicketMessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/support/messages")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class TicketMessageController {

    private final TicketMessageService messageService;

    /**
     * Ajouter un message à un ticket
     */
    @PostMapping("/ticket/{ticketId}/user/{userId}")
    public ResponseEntity<TicketMessageDTO> addMessage(
            @PathVariable Long ticketId,
            @PathVariable Long userId,
            @Valid @RequestBody TicketMessageCreateDTO dto) {

        log.info("Request to add message to ticket {} from user {}", ticketId, userId);
        TicketMessageDTO response = messageService.addMessage(ticketId, userId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Récupérer tous les messages d'un ticket
     */
    @GetMapping("/ticket/{ticketId}")
    public ResponseEntity<List<TicketMessageDTO>> getTicketMessages(@PathVariable Long ticketId) {
        log.info("Request to get messages for ticket: {}", ticketId);
        List<TicketMessageDTO> messages = messageService.getTicketMessages(ticketId);
        return ResponseEntity.ok(messages);
    }

    /**
     * Récupérer un message par son ID
     */
    @GetMapping("/{messageId}")
    public ResponseEntity<TicketMessageDTO> getMessageById(@PathVariable Long messageId) {
        log.info("Request to get message: {}", messageId);
        TicketMessageDTO message = messageService.getMessageById(messageId);
        return ResponseEntity.ok(message);
    }

    /**
     * Mettre à jour un message
     */
    @PutMapping("/{messageId}/user/{userId}")
    public ResponseEntity<TicketMessageDTO> updateMessage(
            @PathVariable Long messageId,
            @PathVariable Long userId,
            @Valid @RequestBody TicketMessageCreateDTO dto) {

        log.info("Request to update message {} by user {}", messageId, userId);
        TicketMessageDTO response = messageService.updateMessage(messageId, userId, dto);
        return ResponseEntity.ok(response);
    }

    /**
     * Supprimer un message
     */
    @DeleteMapping("/{messageId}/user/{userId}")
    public ResponseEntity<Void> deleteMessage(
            @PathVariable Long messageId,
            @PathVariable Long userId,
            @RequestParam(defaultValue = "false") boolean isAdmin) {

        log.info("Request to delete message {} by user {}", messageId, userId);
        messageService.deleteMessage(messageId, userId, isAdmin);
        return ResponseEntity.noContent().build();
    }

    /**
     * Compter les messages d'un ticket
     */
    @GetMapping("/ticket/{ticketId}/count")
    public ResponseEntity<Long> countTicketMessages(@PathVariable Long ticketId) {
        log.info("Request to count messages for ticket: {}", ticketId);
        long count = messageService.countTicketMessages(ticketId);
        return ResponseEntity.ok(count);
    }

    /**
     * Compter les réponses du staff sur un ticket
     */
    @GetMapping("/ticket/{ticketId}/staff/count")
    public ResponseEntity<Long> countStaffReplies(@PathVariable Long ticketId) {
        log.info("Request to count staff replies for ticket: {}", ticketId);
        long count = messageService.countStaffReplies(ticketId);
        return ResponseEntity.ok(count);
    }

    /**
     * Récupérer le dernier message d'un ticket
     */
    @GetMapping("/ticket/{ticketId}/last")
    public ResponseEntity<TicketMessageDTO> getLastMessage(@PathVariable Long ticketId) {
        log.info("Request to get last message for ticket: {}", ticketId);
        TicketMessageDTO message = messageService.getLastMessage(ticketId);
        return message != null ? ResponseEntity.ok(message) : ResponseEntity.noContent().build();
    }
}

