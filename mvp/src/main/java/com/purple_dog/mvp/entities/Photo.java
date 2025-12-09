package com.purple_dog.mvp.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "photos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Photo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    private Integer displayOrder = 0;

    private Boolean isPrimary = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime uploadedAt = LocalDateTime.now();
}

