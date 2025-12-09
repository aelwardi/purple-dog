package com.purple_dog.mvp.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carriers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Carrier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String code;

    private String logo;

    private String apiEndpoint;

    private String trackingUrlPattern;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal basePrice;

    private Boolean active = true;

    @Column(columnDefinition = "TEXT")
    private String description;

    // Relations
    @OneToMany(mappedBy = "carrier", cascade = CascadeType.ALL)
    private List<Delivery> deliveries = new ArrayList<>();
}

