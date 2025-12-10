package com.purple_dog.mvp.dao;

import com.purple_dog.mvp.entities.Delivery;
import com.purple_dog.mvp.entities.DeliveryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

    Optional<Delivery> findByTrackingNumber(String trackingNumber);

    Optional<Delivery> findByOrderId(Long orderId);

    List<Delivery> findByStatus(DeliveryStatus status);

    List<Delivery> findByCarrierId(Long carrierId);

    @Query("SELECT d FROM Delivery d WHERE d.order.buyer.id = :buyerId ORDER BY d.createdAt DESC")
    List<Delivery> findByBuyerId(@Param("buyerId") Long buyerId);

    @Query("SELECT d FROM Delivery d WHERE d.order.seller.id = :sellerId ORDER BY d.createdAt DESC")
    List<Delivery> findBySellerId(@Param("sellerId") Long sellerId);

    @Query("SELECT d FROM Delivery d WHERE d.status = :status AND d.estimatedDeliveryDate < :date")
    List<Delivery> findDelayedDeliveries(@Param("status") DeliveryStatus status, @Param("date") LocalDateTime date);

    @Query("SELECT d FROM Delivery d WHERE d.status IN :statuses ORDER BY d.createdAt DESC")
    List<Delivery> findByStatusIn(@Param("statuses") List<DeliveryStatus> statuses);

    @Query("SELECT COUNT(d) FROM Delivery d WHERE d.carrier.id = :carrierId AND d.status = :status")
    long countByCarrierIdAndStatus(@Param("carrierId") Long carrierId, @Param("status") DeliveryStatus status);

    @Query("SELECT d FROM Delivery d LEFT JOIN FETCH d.order o LEFT JOIN FETCH o.buyer LEFT JOIN FETCH o.seller WHERE d.id = :id")
    Optional<Delivery> findByIdWithDetails(@Param("id") Long id);

    boolean existsByTrackingNumber(String trackingNumber);
}

