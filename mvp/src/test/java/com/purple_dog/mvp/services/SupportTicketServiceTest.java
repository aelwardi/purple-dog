package com.purple_dog.mvp.services;

import com.purple_dog.mvp.dao.PersonRepository;
import com.purple_dog.mvp.dao.SupportTicketRepository;
import com.purple_dog.mvp.dao.TicketMessageRepository;
import com.purple_dog.mvp.dto.SupportTicketCreateDTO;
import com.purple_dog.mvp.dto.SupportTicketResponseDTO;
import com.purple_dog.mvp.entities.*;
import com.purple_dog.mvp.exceptions.InvalidOperationException;
import com.purple_dog.mvp.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SupportTicketServiceTest {

    @Mock
    private SupportTicketRepository ticketRepository;

    @Mock
    private TicketMessageRepository messageRepository;

    @Mock
    private PersonRepository personRepository;

    @InjectMocks
    private SupportTicketService supportTicketService;

    private Individual user;
    private Admin admin;
    private SupportTicket ticket;

    @BeforeEach
    void setUp() {
        user = Individual.builder()
                .id(1L)
                .email("user@example.com")
                .firstName("John")
                .lastName("Doe")
                .role(UserRole.INDIVIDUAL)
                .build();

        admin = Admin.builder()
                .id(5L)
                .email("admin@example.com")
                .firstName("Admin")
                .lastName("Support")
                .role(UserRole.ADMIN)
                .build();

        ticket = SupportTicket.builder()
                .id(1L)
                .ticketNumber("TKT-12345678")
                .user(user)
                .subject("Test Subject")
                .description("Test Description")
                .status(TicketStatus.OPEN)
                .priority(TicketPriority.MEDIUM)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void testCreateTicket_Success() {
        SupportTicketCreateDTO dto = SupportTicketCreateDTO.builder()
                .subject("Help needed")
                .description("I need help with my account")
                .priority(TicketPriority.HIGH)
                .build();

        when(personRepository.findById(1L)).thenReturn(Optional.of(user));
        when(ticketRepository.existsByTicketNumber(any())).thenReturn(false);
        when(ticketRepository.save(any(SupportTicket.class))).thenReturn(ticket);
        when(messageRepository.countByTicketId(any())).thenReturn(0L);
        when(messageRepository.findLastMessageByTicketId(any())).thenReturn(null);

        SupportTicketResponseDTO result = supportTicketService.createTicket(1L, dto);

        assertNotNull(result);
        assertEquals("TKT-12345678", result.getTicketNumber());
        assertEquals(TicketStatus.OPEN, result.getStatus());
        verify(ticketRepository, times(1)).save(any(SupportTicket.class));
    }

    @Test
    void testCreateTicket_UserNotFound() {
        SupportTicketCreateDTO dto = SupportTicketCreateDTO.builder()
                .subject("Help needed")
                .description("I need help")
                .build();

        when(personRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            supportTicketService.createTicket(1L, dto);
        });
    }

    @Test
    void testAssignTicket_Success() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));
        when(personRepository.findById(5L)).thenReturn(Optional.of(admin));
        when(ticketRepository.save(any(SupportTicket.class))).thenReturn(ticket);
        when(messageRepository.countByTicketId(any())).thenReturn(0L);
        when(messageRepository.findLastMessageByTicketId(any())).thenReturn(null);

        SupportTicketResponseDTO result = supportTicketService.assignTicket(1L, 5L);

        assertNotNull(result);
        verify(ticketRepository, times(1)).save(any(SupportTicket.class));
    }

    @Test
    void testCloseTicket_Success() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(any(SupportTicket.class))).thenAnswer(invocation -> {
            SupportTicket saved = invocation.getArgument(0);
            assertEquals(TicketStatus.CLOSED, saved.getStatus());
            assertNotNull(saved.getClosedAt());
            return saved;
        });
        when(messageRepository.countByTicketId(any())).thenReturn(0L);
        when(messageRepository.findLastMessageByTicketId(any())).thenReturn(null);

        SupportTicketResponseDTO result = supportTicketService.closeTicket(1L);

        assertNotNull(result);
        verify(ticketRepository, times(1)).save(any(SupportTicket.class));
    }

    @Test
    void testReopenTicket_Success() {
        ticket.setStatus(TicketStatus.CLOSED);
        ticket.setClosedAt(LocalDateTime.now());

        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(any(SupportTicket.class))).thenReturn(ticket);
        when(messageRepository.countByTicketId(any())).thenReturn(0L);
        when(messageRepository.findLastMessageByTicketId(any())).thenReturn(null);

        SupportTicketResponseDTO result = supportTicketService.reopenTicket(1L);

        assertNotNull(result);
        verify(ticketRepository, times(1)).save(any(SupportTicket.class));
    }

    @Test
    void testReopenTicket_InvalidStatus() {
        ticket.setStatus(TicketStatus.OPEN);

        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));

        assertThrows(InvalidOperationException.class, () -> {
            supportTicketService.reopenTicket(1L);
        });
    }

    @Test
    void testCountActiveUserTickets() {
        when(ticketRepository.countActiveTicketsByUserId(1L)).thenReturn(3L);

        long count = supportTicketService.countActiveUserTickets(1L);

        assertEquals(3L, count);
        verify(ticketRepository, times(1)).countActiveTicketsByUserId(1L);
    }
}

