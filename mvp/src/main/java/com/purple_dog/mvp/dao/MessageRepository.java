package com.purple_dog.mvp.dao;

import com.purple_dog.mvp.entities.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByConversationIdOrderByCreatedAtAsc(Long conversationId);

    @Query("SELECT m FROM Message m WHERE m.conversation.id = :conversationId " +
           "AND m.isBlocked = false ORDER BY m.createdAt ASC")
    List<Message> findNonBlockedMessagesByConversationId(@Param("conversationId") Long conversationId);

    @Query("SELECT COUNT(m) FROM Message m WHERE m.conversation.id = :conversationId " +
           "AND m.sender.id != :userId AND m.isRead = false AND m.isBlocked = false")
    long countUnreadMessagesByConversationAndUser(@Param("conversationId") Long conversationId,
                                                   @Param("userId") Long userId);

    @Query("SELECT COUNT(m) FROM Message m WHERE m.conversation.id IN " +
           "(SELECT c.id FROM Conversation c WHERE c.user1.id = :userId OR c.user2.id = :userId) " +
           "AND m.sender.id != :userId AND m.isRead = false AND m.isBlocked = false")
    long countTotalUnreadMessagesByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE Message m SET m.isRead = true, m.readAt = CURRENT_TIMESTAMP " +
           "WHERE m.conversation.id = :conversationId AND m.sender.id != :userId AND m.isRead = false")
    int markAllAsReadInConversation(@Param("conversationId") Long conversationId,
                                     @Param("userId") Long userId);

    @Query("SELECT m FROM Message m WHERE m.wasFiltered = true ORDER BY m.createdAt DESC")
    List<Message> findFilteredMessages();

    @Query("SELECT m FROM Message m WHERE m.isBlocked = true ORDER BY m.createdAt DESC")
    List<Message> findBlockedMessages();

    @Query("SELECT COUNT(m) FROM Message m WHERE m.sender.id = :userId AND m.wasFiltered = true")
    long countFilteredMessagesByUser(@Param("userId") Long userId);
}

