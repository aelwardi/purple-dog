package com.purple_dog.mvp.dao;

import com.purple_dog.mvp.entities.Offer;
import com.purple_dog.mvp.entities.OfferStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OfferRepository extends JpaRepository<Offer, Long> {

    List<Offer> findByQuickSaleIdOrderByCreatedAtDesc(Long quickSaleId);

    @Query("SELECT o FROM Offer o WHERE o.buyer.id = :buyerId ORDER BY o.createdAt DESC")
    List<Offer> findByBuyerId(@Param("buyerId") Long buyerId);

    @Query("SELECT o FROM Offer o WHERE o.quickSale.product.seller.id = :sellerId ORDER BY o.createdAt DESC")
    List<Offer> findBySellerId(@Param("sellerId") Long sellerId);

    List<Offer> findByStatus(OfferStatus status);

    @Query("SELECT o FROM Offer o WHERE o.quickSale.id = :quickSaleId AND o.status = :status")
    List<Offer> findByQuickSaleIdAndStatus(@Param("quickSaleId") Long quickSaleId, @Param("status") OfferStatus status);

    @Query("SELECT o FROM Offer o WHERE o.buyer.id = :buyerId AND o.quickSale.id = :quickSaleId ORDER BY o.createdAt DESC")
    List<Offer> findByBuyerIdAndQuickSaleId(@Param("buyerId") Long buyerId, @Param("quickSaleId") Long quickSaleId);

    @Query("SELECT o FROM Offer o LEFT JOIN FETCH o.quickSale q LEFT JOIN FETCH q.product LEFT JOIN FETCH o.buyer WHERE o.id = :id")
    Optional<Offer> findByIdWithDetails(@Param("id") Long id);

    @Query("SELECT COUNT(o) FROM Offer o WHERE o.quickSale.id = :quickSaleId")
    long countByQuickSaleId(@Param("quickSaleId") Long quickSaleId);

    @Query("SELECT COUNT(o) FROM Offer o WHERE o.buyer.id = :buyerId")
    long countByBuyerId(@Param("buyerId") Long buyerId);

    @Query("SELECT COUNT(o) FROM Offer o WHERE o.quickSale.id = :quickSaleId AND o.status = 'PENDING'")
    long countPendingByQuickSaleId(@Param("quickSaleId") Long quickSaleId);
}

