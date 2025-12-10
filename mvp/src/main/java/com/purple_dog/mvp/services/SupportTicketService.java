package com.purple_dog.mvp.services;

import com.purple_dog.mvp.dao.PersonRepository;
import com.purple_dog.mvp.dao.SupportTicketRepository;
import com.purple_dog.mvp.dao.TicketMessageRepository;
import com.purple_dog.mvp.dto.*;
import com.purple_dog.mvp.entities.*;
import com.purple_dog.mvp.exceptions.InvalidOperationException;
import com.purple_dog.mvp.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SupportTicketService {

    private final SupportTicketRepository ticketRepository;
    private final TicketMessageRepository messageRepository;
    private final PersonRepository personRepository;

    /**
     * Créer un nouveau ticket de support
     */
    public SupportTicketResponseDTO createTicket(Long userId, SupportTicketCreateDTO dto) {
        log.info("Creating support ticket for user: {}", userId);

        Person user = personRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Générer un numéro de ticket unique
        String ticketNumber = generateUniqueTicketNumber();

        // Créer le ticket
        SupportTicket ticket = SupportTicket.builder()
                .ticketNumber(ticketNumber)
                .user(user)
                .subject(dto.getSubject())
                .description(dto.getDescription())
                .status(TicketStatus.OPEN)
                .priority(dto.getPriority() != null ? dto.getPriority() : TicketPriority.MEDIUM)
                .category(dto.getCategory())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        ticket = ticketRepository.save(ticket);
        log.info("Support ticket created successfully with number: {}", ticketNumber);

        return mapToResponseDTO(ticket);
    }

    /**
     * Récupérer tous les tickets d'un utilisateur
     */
    public List<SupportTicketResponseDTO> getUserTickets(Long userId) {
        log.info("Fetching tickets for user: {}", userId);

        if (!personRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }

        return ticketRepository.findByUserIdOrderByUpdatedAtDesc(userId).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupérer les tickets assignés à un admin
     */
    public List<SupportTicketResponseDTO> getAdminTickets(Long adminId) {
        log.info("Fetching tickets for admin: {}", adminId);

        return ticketRepository.findByAssignedAdminIdOrderByPriorityAndUpdatedAt(adminId).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupérer tous les tickets non assignés
     */
    public List<SupportTicketResponseDTO> getUnassignedTickets() {
        log.info("Fetching unassigned tickets");

        return ticketRepository.findUnassignedOpenTickets().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupérer un ticket par son numéro
     */
    public SupportTicketResponseDTO getTicketByNumber(String ticketNumber) {
        log.info("Fetching ticket: {}", ticketNumber);

        SupportTicket ticket = ticketRepository.findByTicketNumber(ticketNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found: " + ticketNumber));

        return mapToResponseDTO(ticket);
    }

    /**
     * Récupérer un ticket par son ID
     */
    public SupportTicketResponseDTO getTicketById(Long ticketId) {
        log.info("Fetching ticket with id: {}", ticketId);

        SupportTicket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + ticketId));

        return mapToResponseDTO(ticket);
    }

    /**
     * Mettre à jour un ticket
     */
    public SupportTicketResponseDTO updateTicket(Long ticketId, SupportTicketUpdateDTO dto) {
        log.info("Updating ticket: {}", ticketId);

        SupportTicket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + ticketId));

        if (dto.getSubject() != null) {
            ticket.setSubject(dto.getSubject());
        }
        if (dto.getDescription() != null) {
            ticket.setDescription(dto.getDescription());
        }
        if (dto.getStatus() != null) {
            updateTicketStatus(ticket, dto.getStatus());
        }
        if (dto.getPriority() != null) {
            ticket.setPriority(dto.getPriority());
        }
        if (dto.getCategory() != null) {
            ticket.setCategory(dto.getCategory());
        }
        if (dto.getAssignedAdminId() != null) {
            assignTicketToAdmin(ticket, dto.getAssignedAdminId());
        }

        ticket.setUpdatedAt(LocalDateTime.now());
        ticket = ticketRepository.save(ticket);

        log.info("Ticket updated successfully: {}", ticketId);
        return mapToResponseDTO(ticket);
    }

    /**
     * Assigner un ticket à un admin
     */
    public SupportTicketResponseDTO assignTicket(Long ticketId, Long adminId) {
        log.info("Assigning ticket {} to admin {}", ticketId, adminId);

        SupportTicket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + ticketId));

        assignTicketToAdmin(ticket, adminId);

        if (ticket.getStatus() == TicketStatus.OPEN) {
            ticket.setStatus(TicketStatus.IN_PROGRESS);
        }

        ticket.setUpdatedAt(LocalDateTime.now());
        ticket = ticketRepository.save(ticket);

        log.info("Ticket assigned successfully");
        return mapToResponseDTO(ticket);
    }

    /**
     * Changer le statut d'un ticket
     */
    public SupportTicketResponseDTO changeTicketStatus(Long ticketId, TicketStatus newStatus) {
        log.info("Changing ticket {} status to {}", ticketId, newStatus);

        SupportTicket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + ticketId));

        updateTicketStatus(ticket, newStatus);
        ticket.setUpdatedAt(LocalDateTime.now());
        ticket = ticketRepository.save(ticket);

        log.info("Ticket status changed successfully");
        return mapToResponseDTO(ticket);
    }

    /**
     * Fermer un ticket
     */
    public SupportTicketResponseDTO closeTicket(Long ticketId) {
        return changeTicketStatus(ticketId, TicketStatus.CLOSED);
    }

    /**
     * Rouvrir un ticket
     */
    public SupportTicketResponseDTO reopenTicket(Long ticketId) {
        log.info("Reopening ticket: {}", ticketId);

        SupportTicket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + ticketId));

        if (ticket.getStatus() != TicketStatus.CLOSED && ticket.getStatus() != TicketStatus.RESOLVED) {
            throw new InvalidOperationException("Only closed or resolved tickets can be reopened");
        }

        ticket.setStatus(TicketStatus.OPEN);
        ticket.setResolvedAt(null);
        ticket.setClosedAt(null);
        ticket.setUpdatedAt(LocalDateTime.now());
        ticket = ticketRepository.save(ticket);

        log.info("Ticket reopened successfully");
        return mapToResponseDTO(ticket);
    }

    /**
     * Compter les tickets actifs d'un utilisateur
     */
    public long countActiveUserTickets(Long userId) {
        return ticketRepository.countActiveTicketsByUserId(userId);
    }

    /**
     * Compter les tickets actifs d'un admin
     */
    public long countActiveAdminTickets(Long adminId) {
        return ticketRepository.countActiveTicketsByAdminId(adminId);
    }

    /**
     * Récupérer les tickets par statut
     */
    public List<SupportTicketResponseDTO> getTicketsByStatus(TicketStatus status) {
        log.info("Fetching tickets with status: {}", status);

        return ticketRepository.findByStatus(status).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Supprimer un ticket (admin seulement)
     */
    public void deleteTicket(Long ticketId) {
        log.info("Deleting ticket: {}", ticketId);

        SupportTicket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + ticketId));

        ticketRepository.delete(ticket);
        log.info("Ticket deleted successfully");
    }

    // Méthodes privées

    private void assignTicketToAdmin(SupportTicket ticket, Long adminId) {
        Admin admin = (Admin) personRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found with id: " + adminId));

        ticket.setAssignedAdmin(admin);
    }

    private void updateTicketStatus(SupportTicket ticket, TicketStatus newStatus) {
        ticket.setStatus(newStatus);

        if (newStatus == TicketStatus.RESOLVED) {
            ticket.setResolvedAt(LocalDateTime.now());
        } else if (newStatus == TicketStatus.CLOSED) {
            ticket.setClosedAt(LocalDateTime.now());
            if (ticket.getResolvedAt() == null) {
                ticket.setResolvedAt(LocalDateTime.now());
            }
        }
    }

    private String generateUniqueTicketNumber() {
        String ticketNumber;
        do {
            ticketNumber = "TKT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (ticketRepository.existsByTicketNumber(ticketNumber));
        return ticketNumber;
    }

    private SupportTicketResponseDTO mapToResponseDTO(SupportTicket ticket) {
        long messageCount = messageRepository.countByTicketId(ticket.getId());
        TicketMessage lastMessage = messageRepository.findLastMessageByTicketId(ticket.getId());

        return SupportTicketResponseDTO.builder()
                .id(ticket.getId())
                .ticketNumber(ticket.getTicketNumber())
                .userId(ticket.getUser().getId())
                .userFullName(ticket.getUser().getFirstName() + " " + ticket.getUser().getLastName())
                .userEmail(ticket.getUser().getEmail())
                .assignedAdminId(ticket.getAssignedAdmin() != null ? ticket.getAssignedAdmin().getId() : null)
                .assignedAdminName(ticket.getAssignedAdmin() != null ?
                        ticket.getAssignedAdmin().getFirstName() + " " + ticket.getAssignedAdmin().getLastName() : null)
                .subject(ticket.getSubject())
                .description(ticket.getDescription())
                .status(ticket.getStatus())
                .priority(ticket.getPriority())
                .category(ticket.getCategory())
                .createdAt(ticket.getCreatedAt())
                .updatedAt(ticket.getUpdatedAt())
                .resolvedAt(ticket.getResolvedAt())
                .closedAt(ticket.getClosedAt())
                .messageCount(messageCount)
                .lastMessage(lastMessage != null ? mapMessageToDTO(lastMessage) : null)
                .build();
    }

    private TicketMessageDTO mapMessageToDTO(TicketMessage message) {
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

