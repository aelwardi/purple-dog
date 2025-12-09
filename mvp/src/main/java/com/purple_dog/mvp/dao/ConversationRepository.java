package com.purple_dog.mvp.dao;

import com.purple_dog.mvp.entities.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    @Query("SELECT c FROM Conversation c WHERE " +
           "(c.user1.id = :userId OR c.user2.id = :userId) " +
           "AND ((c.user1.id = :userId AND c.user1Archived = false) OR (c.user2.id = :userId AND c.user2Archived = false)) " +
           "ORDER BY c.lastMessageAt DESC NULLS LAST")
    List<Conversation> findActiveConversationsByUserId(@Param("userId") Long userId);

    @Query("SELECT c FROM Conversation c WHERE " +
           "((c.user1.id = :user1Id AND c.user2.id = :user2Id) OR " +
           "(c.user1.id = :user2Id AND c.user2.id = :user1Id))")
    Optional<Conversation> findConversationBetweenUsers(@Param("user1Id") Long user1Id, @Param("user2Id") Long user2Id);

    @Query("SELECT c FROM Conversation c WHERE c.order.id = :orderId")
    Optional<Conversation> findByOrderId(@Param("orderId") Long orderId);

    @Query("SELECT COUNT(c) FROM Conversation c WHERE " +
           "(c.user1.id = :userId AND c.user1Archived = false) OR " +
           "(c.user2.id = :userId AND c.user2Archived = false)")
    long countActiveConversationsByUserId(@Param("userId") Long userId);

    @Query("SELECT c FROM Conversation c JOIN c.messages m WHERE " +
           "(c.user1.id = :userId OR c.user2.id = :userId) " +
           "AND m.isRead = false AND m.sender.id != :userId")
    List<Conversation> findConversationsWithUnreadMessages(@Param("userId") Long userId);
}

