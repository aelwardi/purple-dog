package com.purple_dog.mvp.dao;

import com.purple_dog.mvp.entities.TicketMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketMessageRepository extends JpaRepository<TicketMessage, Long> {

    List<TicketMessage> findBySupportTicketIdOrderByCreatedAtAsc(Long ticketId);

    List<TicketMessage> findBySupportTicketIdOrderByCreatedAtDesc(Long ticketId);

    @Query("SELECT m FROM TicketMessage m WHERE m.supportTicket.id = :ticketId AND m.sender.id = :senderId ORDER BY m.createdAt ASC")
    List<TicketMessage> findByTicketIdAndSenderId(@Param("ticketId") Long ticketId, @Param("senderId") Long senderId);

    @Query("SELECT COUNT(m) FROM TicketMessage m WHERE m.supportTicket.id = :ticketId")
    long countByTicketId(@Param("ticketId") Long ticketId);

    @Query("SELECT COUNT(m) FROM TicketMessage m WHERE m.supportTicket.id = :ticketId AND m.isStaffReply = true")
    long countStaffRepliesByTicketId(@Param("ticketId") Long ticketId);

    @Query("SELECT m FROM TicketMessage m WHERE m.supportTicket.id = :ticketId ORDER BY m.createdAt DESC LIMIT 1")
    TicketMessage findLastMessageByTicketId(@Param("ticketId") Long ticketId);
}

