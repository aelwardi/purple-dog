package com.purple_dog.mvp.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "quick_sales")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuickSale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal fixedPrice;

    private Boolean acceptOffers = true;

    @Column(precision = 10, scale = 2)
    private BigDecimal minimumOfferPrice;

    private Boolean isAvailable = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime soldAt;

    // Relations
    @OneToMany(mappedBy = "quickSale", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Offer> offers = new ArrayList<>();

    @OneToOne(mappedBy = "quickSale")
    private Order order;

    @PrePersist
    protected void onPrePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.isAvailable == null) {
            this.isAvailable = true;
        }
        if (this.acceptOffers == null) {
            this.acceptOffers = Boolean.TRUE;
        }
    }

}