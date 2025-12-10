package com.purple_dog.mvp.web.rest;

import com.purple_dog.mvp.dto.SupportTicketCreateDTO;
import com.purple_dog.mvp.dto.SupportTicketResponseDTO;
import com.purple_dog.mvp.dto.SupportTicketUpdateDTO;
import com.purple_dog.mvp.entities.TicketStatus;
import com.purple_dog.mvp.services.SupportTicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/support/tickets")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class SupportTicketController {

    private final SupportTicketService ticketService;

    /**
     * Créer un nouveau ticket de support
     */
    @PostMapping("/user/{userId}")
    public ResponseEntity<SupportTicketResponseDTO> createTicket(
            @PathVariable Long userId,
            @Valid @RequestBody SupportTicketCreateDTO dto) {

        log.info("Request to create ticket for user: {}", userId);
        SupportTicketResponseDTO response = ticketService.createTicket(userId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Récupérer tous les tickets d'un utilisateur
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<SupportTicketResponseDTO>> getUserTickets(@PathVariable Long userId) {
        log.info("Request to get tickets for user: {}", userId);
        List<SupportTicketResponseDTO> tickets = ticketService.getUserTickets(userId);
        return ResponseEntity.ok(tickets);
    }

    /**
     * Récupérer les tickets assignés à un admin
     */
    @GetMapping("/admin/{adminId}")
    public ResponseEntity<List<SupportTicketResponseDTO>> getAdminTickets(@PathVariable Long adminId) {
        log.info("Request to get tickets for admin: {}", adminId);
        List<SupportTicketResponseDTO> tickets = ticketService.getAdminTickets(adminId);
        return ResponseEntity.ok(tickets);
    }

    /**
     * Récupérer tous les tickets non assignés (Admin)
     */
    @GetMapping("/unassigned")
    public ResponseEntity<List<SupportTicketResponseDTO>> getUnassignedTickets() {
        log.info("Request to get unassigned tickets");
        List<SupportTicketResponseDTO> tickets = ticketService.getUnassignedTickets();
        return ResponseEntity.ok(tickets);
    }

    /**
     * Récupérer un ticket par son numéro
     */
    @GetMapping("/number/{ticketNumber}")
    public ResponseEntity<SupportTicketResponseDTO> getTicketByNumber(@PathVariable String ticketNumber) {
        log.info("Request to get ticket: {}", ticketNumber);
        SupportTicketResponseDTO ticket = ticketService.getTicketByNumber(ticketNumber);
        return ResponseEntity.ok(ticket);
    }

    /**
     * Récupérer un ticket par son ID
     */
    @GetMapping("/{ticketId}")
    public ResponseEntity<SupportTicketResponseDTO> getTicketById(@PathVariable Long ticketId) {
        log.info("Request to get ticket with id: {}", ticketId);
        SupportTicketResponseDTO ticket = ticketService.getTicketById(ticketId);
        return ResponseEntity.ok(ticket);
    }

    /**
     * Récupérer les tickets par statut
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<SupportTicketResponseDTO>> getTicketsByStatus(@PathVariable TicketStatus status) {
        log.info("Request to get tickets with status: {}", status);
        List<SupportTicketResponseDTO> tickets = ticketService.getTicketsByStatus(status);
        return ResponseEntity.ok(tickets);
    }

    /**
     * Mettre à jour un ticket
     */
    @PutMapping("/{ticketId}")
    public ResponseEntity<SupportTicketResponseDTO> updateTicket(
            @PathVariable Long ticketId,
            @Valid @RequestBody SupportTicketUpdateDTO dto) {

        log.info("Request to update ticket: {}", ticketId);
        SupportTicketResponseDTO response = ticketService.updateTicket(ticketId, dto);
        return ResponseEntity.ok(response);
    }

    /**
     * Assigner un ticket à un admin
     */
    @PutMapping("/{ticketId}/assign/{adminId}")
    public ResponseEntity<SupportTicketResponseDTO> assignTicket(
            @PathVariable Long ticketId,
            @PathVariable Long adminId) {

        log.info("Request to assign ticket {} to admin {}", ticketId, adminId);
        SupportTicketResponseDTO response = ticketService.assignTicket(ticketId, adminId);
        return ResponseEntity.ok(response);
    }

    /**
     * Changer le statut d'un ticket
     */
    @PutMapping("/{ticketId}/status/{status}")
    public ResponseEntity<SupportTicketResponseDTO> changeTicketStatus(
            @PathVariable Long ticketId,
            @PathVariable TicketStatus status) {

        log.info("Request to change ticket {} status to {}", ticketId, status);
        SupportTicketResponseDTO response = ticketService.changeTicketStatus(ticketId, status);
        return ResponseEntity.ok(response);
    }

    /**
     * Fermer un ticket
     */
    @PutMapping("/{ticketId}/close")
    public ResponseEntity<SupportTicketResponseDTO> closeTicket(@PathVariable Long ticketId) {
        log.info("Request to close ticket: {}", ticketId);
        SupportTicketResponseDTO response = ticketService.closeTicket(ticketId);
        return ResponseEntity.ok(response);
    }

    /**
     * Rouvrir un ticket
     */
    @PutMapping("/{ticketId}/reopen")
    public ResponseEntity<SupportTicketResponseDTO> reopenTicket(@PathVariable Long ticketId) {
        log.info("Request to reopen ticket: {}", ticketId);
        SupportTicketResponseDTO response = ticketService.reopenTicket(ticketId);
        return ResponseEntity.ok(response);
    }

    /**
     * Compter les tickets actifs d'un utilisateur
     */
    @GetMapping("/user/{userId}/active/count")
    public ResponseEntity<Long> countActiveUserTickets(@PathVariable Long userId) {
        log.info("Request to count active tickets for user: {}", userId);
        long count = ticketService.countActiveUserTickets(userId);
        return ResponseEntity.ok(count);
    }

    /**
     * Compter les tickets actifs d'un admin
     */
    @GetMapping("/admin/{adminId}/active/count")
    public ResponseEntity<Long> countActiveAdminTickets(@PathVariable Long adminId) {
        log.info("Request to count active tickets for admin: {}", adminId);
        long count = ticketService.countActiveAdminTickets(adminId);
        return ResponseEntity.ok(count);
    }

    /**
     * Supprimer un ticket (Admin seulement)
     */
    @DeleteMapping("/{ticketId}")
    public ResponseEntity<Void> deleteTicket(@PathVariable Long ticketId) {
        log.info("Request to delete ticket: {}", ticketId);
        ticketService.deleteTicket(ticketId);
        return ResponseEntity.noContent().build();
    }
}

