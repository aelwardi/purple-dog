package com.purple_dog.mvp.dao;

import com.purple_dog.mvp.entities.Order;
import com.purple_dog.mvp.entities.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderNumber(String orderNumber);

    List<Order> findByBuyerId(Long buyerId);

    List<Order> findBySellerId(Long sellerId);

    List<Order> findByStatus(OrderStatus status);

    List<Order> findByBuyerIdAndStatus(Long buyerId, OrderStatus status);

    List<Order> findBySellerIdAndStatus(Long sellerId, OrderStatus status);

    @Query("SELECT o FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate")
    List<Order> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, 
                                       @Param("endDate") LocalDateTime endDate);

    @Query("SELECT o FROM Order o WHERE o.buyer.id = :personId OR o.seller.id = :personId")
    List<Order> findByPersonId(@Param("personId") Long personId);

    long countByStatus(OrderStatus status);

    long countByBuyerId(Long buyerId);

    long countBySellerId(Long sellerId);

    boolean existsByOrderNumber(String orderNumber);
}
