package com.purple_dog.mvp.dao;

import com.purple_dog.mvp.entities.Bid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BidRepository extends JpaRepository<Bid, Long> {

    List<Bid> findByAuctionIdOrderByAmountDesc(Long auctionId);

    List<Bid> findByBidderIdOrderByBidDateDesc(Long bidderId);

    @Query("SELECT b FROM Bid b WHERE b.auction.id = :auctionId AND b.isWinning = true")
    Optional<Bid> findCurrentWinningBid(@Param("auctionId") Long auctionId);

    @Query("SELECT b FROM Bid b WHERE b.auction.id = :auctionId AND b.bidder.id = :bidderId ORDER BY b.amount DESC")
    List<Bid> findByAuctionAndBidder(@Param("auctionId") Long auctionId, @Param("bidderId") Long bidderId);

    @Query("SELECT COUNT(b) FROM Bid b WHERE b.auction.id = :auctionId")
    Integer countBidsByAuction(@Param("auctionId") Long auctionId);
}
