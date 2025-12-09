package com.purple_dog.mvp.dao;

import com.purple_dog.mvp.entities.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);

    Optional<Invoice> findByOrderId(Long orderId);

    @Query("SELECT i FROM Invoice i WHERE i.order.buyer.id = :buyerId ORDER BY i.issuedAt DESC")
    List<Invoice> findByBuyerId(@Param("buyerId") Long buyerId);

    @Query("SELECT i FROM Invoice i WHERE i.order.seller.id = :sellerId ORDER BY i.issuedAt DESC")
    List<Invoice> findBySellerId(@Param("sellerId") Long sellerId);

    @Query("SELECT i FROM Invoice i WHERE i.issuedAt BETWEEN :startDate AND :endDate ORDER BY i.issuedAt DESC")
    List<Invoice> findByDateRange(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT i FROM Invoice i LEFT JOIN FETCH i.order o LEFT JOIN FETCH o.buyer WHERE i.id = :id")
    Optional<Invoice> findByIdWithDetails(@Param("id") Long id);

    boolean existsByInvoiceNumber(String invoiceNumber);

    boolean existsByOrderId(Long orderId);

    @Query("SELECT COUNT(i) FROM Invoice i WHERE i.order.buyer.id = :buyerId")
    long countByBuyerId(@Param("buyerId") Long buyerId);

    @Query("SELECT COUNT(i) FROM Invoice i WHERE i.order.seller.id = :sellerId")
    long countBySellerId(@Param("sellerId") Long sellerId);
}

