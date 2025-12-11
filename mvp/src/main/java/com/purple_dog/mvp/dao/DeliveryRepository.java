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

    // Find by tracking number
    Optional<Delivery> findByTrackingNumber(String trackingNumber);
    Optional<Delivery> findByShippoTrackingNumber(String shippoTrackingNumber);

    // Find by Shippo IDs
    Optional<Delivery> findByShippoShipmentId(String shippoShipmentId);
    Optional<Delivery> findByShippoTransactionId(String shippoTransactionId);

    // Find by order
    Optional<Delivery> findByOrderId(Long orderId);
    List<Delivery> findByOrderIdIn(List<Long> orderIds);

    // Find by status
    List<Delivery> findByStatus(DeliveryStatus status);
    List<Delivery> findByStatusIn(List<DeliveryStatus> statuses);

    // Find by carrier
    List<Delivery> findByCarrierId(Long carrierId);
    List<Delivery> findByCarrierName(String carrierName);

    // Find by buyer
    @Query("SELECT d FROM Delivery d WHERE d.order.buyer.id = :buyerId ORDER BY d.createdAt DESC")
    List<Delivery> findByBuyerId(@Param("buyerId") Long buyerId);

    // Find by seller
    @Query("SELECT d FROM Delivery d WHERE d.order.seller.id = :sellerId ORDER BY d.createdAt DESC")
    List<Delivery> findBySellerId(@Param("sellerId") Long sellerId);

    // Find delayed deliveries
    @Query("SELECT d FROM Delivery d WHERE d.status = :status AND d.estimatedDeliveryDate < :date")
    List<Delivery> findDelayedDeliveries(@Param("status") DeliveryStatus status, @Param("date") LocalDateTime date);

    // Count by status
    long countByStatus(DeliveryStatus status);

    // Find recent deliveries
    @Query("SELECT d FROM Delivery d WHERE d.createdAt >= :since ORDER BY d.createdAt DESC")
    List<Delivery> findRecentDeliveries(@Param("since") LocalDateTime since);

    @Query("SELECT COUNT(d) FROM Delivery d WHERE d.carrier.id = :carrierId AND d.status = :status")
    long countByCarrierIdAndStatus(@Param("carrierId") Long carrierId, @Param("status") DeliveryStatus status);

    @Query("SELECT d FROM Delivery d LEFT JOIN FETCH d.order o LEFT JOIN FETCH o.buyer LEFT JOIN FETCH o.seller WHERE d.id = :id")
    Optional<Delivery> findByIdWithDetails(@Param("id") Long id);

    boolean existsByTrackingNumber(String trackingNumber);
}

