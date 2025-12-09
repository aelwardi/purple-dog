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
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private Person seller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(precision = 10, scale = 2)
    private BigDecimal widthCm;

    @Column(precision = 10, scale = 2)
    private BigDecimal heightCm;

    @Column(precision = 10, scale = 2)
    private BigDecimal depthCm;

    @Column(precision = 10, scale = 2)
    private BigDecimal weightKg;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductCondition productCondition;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status = ProductStatus.DRAFT;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SaleType saleType;

    @Column(precision = 10, scale = 2)
    private BigDecimal estimatedValue;

    private String brand;

    private Integer yearOfManufacture;

    private String origin;

    @Column(columnDefinition = "TEXT")
    private String authenticityCertificate;

    private Boolean hasDocumentation = false;

    private Integer viewCount = 0;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt = LocalDateTime.now();

    private LocalDateTime validatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "validated_by_admin_id")
    private Admin validatedBy;

    // Relations
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Photo> photos = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Document> documents = new ArrayList<>();

    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private Auction auction;

    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private QuickSale quickSale;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<Favorite> favorites = new ArrayList<>();

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
