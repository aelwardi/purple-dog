package com.purple_dog.mvp.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entity to store Stripe customer information
 * Links Purple Dog users with Stripe customers
 */
@Entity
@Table(name = "stripe_customers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StripeCustomer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private Person user;

    @Column(nullable = false, unique = true)
    private String stripeCustomerId;

    @Column(nullable = false)
    private String email;

    private String defaultPaymentMethodId;

    @Column(length = 1000)
    private String metadata;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}

