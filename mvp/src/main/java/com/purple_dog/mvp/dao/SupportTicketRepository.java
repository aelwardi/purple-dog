package com.purple_dog.mvp.dao;

import com.purple_dog.mvp.entities.SupportTicket;
import com.purple_dog.mvp.entities.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SupportTicketRepository extends JpaRepository<SupportTicket, Long> {

    Optional<SupportTicket> findByTicketNumber(String ticketNumber);

    List<SupportTicket> findByUserId(Long userId);

    List<SupportTicket> findByUserIdAndStatus(Long userId, TicketStatus status);

    List<SupportTicket> findByAssignedAdminId(Long adminId);

    List<SupportTicket> findByAssignedAdminIdAndStatus(Long adminId, TicketStatus status);

    List<SupportTicket> findByStatus(TicketStatus status);

    List<SupportTicket> findByCategory(String category);

    @Query("SELECT t FROM SupportTicket t WHERE t.assignedAdmin IS NULL AND t.status = 'OPEN' ORDER BY t.priority DESC, t.createdAt ASC")
    List<SupportTicket> findUnassignedOpenTickets();

    @Query("SELECT t FROM SupportTicket t WHERE t.user.id = :userId ORDER BY t.updatedAt DESC")
    List<SupportTicket> findByUserIdOrderByUpdatedAtDesc(@Param("userId") Long userId);

    @Query("SELECT t FROM SupportTicket t WHERE t.assignedAdmin.id = :adminId ORDER BY t.priority DESC, t.updatedAt DESC")
    List<SupportTicket> findByAssignedAdminIdOrderByPriorityAndUpdatedAt(@Param("adminId") Long adminId);

    @Query("SELECT COUNT(t) FROM SupportTicket t WHERE t.user.id = :userId AND t.status IN ('OPEN', 'IN_PROGRESS')")
    long countActiveTicketsByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(t) FROM SupportTicket t WHERE t.assignedAdmin.id = :adminId AND t.status IN ('OPEN', 'IN_PROGRESS')")
    long countActiveTicketsByAdminId(@Param("adminId") Long adminId);

    @Query("SELECT t FROM SupportTicket t WHERE t.updatedAt < :date AND t.status = :status")
    List<SupportTicket> findByStatusAndUpdatedAtBefore(@Param("status") TicketStatus status, @Param("date") LocalDateTime date);

    boolean existsByTicketNumber(String ticketNumber);
}

