package com.purple_dog.mvp.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bids")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bid {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auction_id", nullable = false)
    private Auction auction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bidder_id", nullable = false)
    private Person bidder;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(precision = 10, scale = 2)
    private BigDecimal maxAmount; // Pour enchères automatiques

    private Boolean isAutoBid = false;

    @Column(name = "bid_date")
    private LocalDateTime bidDate = LocalDateTime.now();

    private Boolean isWinning = false;
}
