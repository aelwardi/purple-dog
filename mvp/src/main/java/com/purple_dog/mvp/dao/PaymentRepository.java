package com.purple_dog.mvp.dao;

import com.purple_dog.mvp.entities.Payment;
import com.purple_dog.mvp.entities.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByOrderId(Long orderId);

    Optional<Payment> findByPaymentIntentId(String paymentIntentId);

    Optional<Payment> findByStripeChargeId(String stripeChargeId);

    List<Payment> findByStatus(PaymentStatus status);

    @Query("SELECT p FROM Payment p WHERE p.order.buyer.id = :userId ORDER BY p.createdAt DESC")
    List<Payment> findByUserId(@Param("userId") Long userId);

    @Query("SELECT p FROM Payment p WHERE p.order.seller.id = :sellerId AND p.status = 'SUCCEEDED' ORDER BY p.createdAt DESC")
    List<Payment> findSuccessfulPaymentsBySellerId(@Param("sellerId") Long sellerId);

    @Query("SELECT p FROM Payment p WHERE p.status = :status AND p.createdAt BETWEEN :startDate AND :endDate")
    List<Payment> findByStatusAndDateRange(
        @Param("status") PaymentStatus status,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.order.seller.id = :sellerId AND p.status = 'SUCCEEDED'")
    BigDecimal calculateTotalEarningsBySellerId(@Param("sellerId") Long sellerId);

    @Query("SELECT COUNT(p) FROM Payment p WHERE p.status = :status")
    long countByStatus(@Param("status") PaymentStatus status);

    @Query("SELECT p FROM Payment p LEFT JOIN FETCH p.order o LEFT JOIN FETCH o.buyer WHERE p.id = :id")
    Optional<Payment> findByIdWithDetails(@Param("id") Long id);

    boolean existsByOrderId(Long orderId);
}

