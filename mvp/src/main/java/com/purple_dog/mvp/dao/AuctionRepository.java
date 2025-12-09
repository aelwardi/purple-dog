package com.purple_dog.mvp.dao;

import com.purple_dog.mvp.entities.Auction;
import com.purple_dog.mvp.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AuctionRepository extends JpaRepository<Auction, Long> {

    /**
     * Récupère toutes les enchères
     */
    List<Auction> findAll();

    /**
     * Récupère une enchère par son produit
     */
    Optional<Auction> findByProduct(Product product);

    /**
     * Récupère les enchères actives
     */
    @Query("SELECT a FROM Auction a WHERE a.isActive = true AND a.endDate > CURRENT_TIMESTAMP")
    List<Auction> findActiveAuctions();

    /**
     * Récupère les enchères terminées
     */
    @Query("SELECT a FROM Auction a WHERE a.isActive = false OR a.endDate <= CURRENT_TIMESTAMP")
    List<Auction> findClosedAuctions();

    /**
     * Récupère les enchères où le prix de réserve n'a pas été atteint
     */
    @Query("SELECT a FROM Auction a WHERE a.reservePriceMet = false")
    List<Auction> findAuctionsWithoutReserveMet();

    /**
     * Récupère les enchères où le prix de réserve a été atteint
     */
    @Query("SELECT a FROM Auction a WHERE a.reservePriceMet = true")
    List<Auction> findAuctionsWithReserveMet();

    /**
     * Récupère les enchères par plage de dates
     */
    @Query("SELECT a FROM Auction a WHERE a.startDate >= :startDate AND a.startDate <= :endDate")
    List<Auction> findAuctionsByDateRange(@Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}