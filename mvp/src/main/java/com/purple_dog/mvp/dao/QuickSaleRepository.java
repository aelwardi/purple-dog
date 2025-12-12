package com.purple_dog.mvp.dao;

import com.purple_dog.mvp.entities.QuickSale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface QuickSaleRepository extends JpaRepository<QuickSale, Long> {

    Optional<QuickSale> findByProductId(Long productId);

    List<QuickSale> findByIsAvailableTrue();

    @Query("SELECT q FROM QuickSale q WHERE q.product.seller.id = :sellerId ORDER BY q.createdAt DESC")
    List<QuickSale> findBySellerId(@Param("sellerId") Long sellerId);

    @Query("SELECT q FROM QuickSale q WHERE q.isAvailable = true AND q.fixedPrice <= :maxPrice ORDER BY q.fixedPrice ASC")
    List<QuickSale> findAvailableByMaxPrice(@Param("maxPrice") BigDecimal maxPrice);

    @Query("SELECT q FROM QuickSale q WHERE q.isAvailable = true AND q.acceptOffers = true ORDER BY q.createdAt DESC")
    List<QuickSale> findAvailableAcceptingOffers();

    @Query("SELECT q FROM QuickSale q LEFT JOIN FETCH q.product p LEFT JOIN FETCH p.seller WHERE q.id = :id")
    Optional<QuickSale> findByIdWithDetails(@Param("id") Long id);

    @Query("SELECT q FROM QuickSale q LEFT JOIN FETCH q.product p LEFT JOIN FETCH p.seller WHERE p.seller.id = :sellerId AND q.fixedPrice = :price AND q.isAvailable = true")
    Optional<QuickSale> findAvailableBySellerAndPrice(@Param("sellerId") Long sellerId, @Param("price") java.math.BigDecimal price);

    @Query("SELECT COUNT(q) FROM QuickSale q WHERE q.product.seller.id = :sellerId")
    long countBySellerId(@Param("sellerId") Long sellerId);

    @Query("SELECT COUNT(q) FROM QuickSale q WHERE q.product.seller.id = :sellerId AND q.isAvailable = false")
    long countSoldBySellerId(@Param("sellerId") Long sellerId);

    boolean existsByProductId(Long productId);
}
