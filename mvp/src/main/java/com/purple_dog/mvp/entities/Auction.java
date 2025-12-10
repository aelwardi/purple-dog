package com.purple_dog.mvp.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "auctions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Auction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal startingPrice;

    @Column(precision = 10, scale = 2)
    private BigDecimal reservePrice;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal currentPrice;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal bidIncrement = new BigDecimal("10.00");

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    private AuctionStatus status = AuctionStatus.PENDING;

    private Boolean isAutoExtendEnabled = true; // Extension de 10min si enchère à h-1

    private Boolean reservePriceMet = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "winner_id")
    private Professional winner;

    private Integer totalBids = 0;

    // Relations
    @OneToMany(mappedBy = "auction", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Bid> bids = new ArrayList<>();

    @OneToOne(mappedBy = "auction")
    private Order order;
}
