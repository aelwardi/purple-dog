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
@Table(name = "platform")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Platform {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String phone;

    @Column(columnDefinition = "TEXT")
    private String address;

    private String website;

    private String logoUrl;

    @Column(nullable = false)
    @Builder.Default
    private Long totalUsers = 0L;

    @Column(nullable = false)
    @Builder.Default
    private Long totalProducts = 0L;

    @Column(nullable = false)
    @Builder.Default
    private Long totalTransactions = 0L;

    @Column(nullable = false, precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal totalRevenue = BigDecimal.ZERO;

    @Column(nullable = false, precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal platformCommission = new BigDecimal("5.00");

    private String termsOfServiceUrl;
    private String privacyPolicyUrl;
    private String companyRegistration;
    private String vatNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private PlatformStatus status = PlatformStatus.ACTIVE;

    @OneToMany(mappedBy = "platform", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PlatformReview> reviews = new ArrayList<>();

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}