package com.purple_dog.mvp.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "professionals")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Professional extends Person {

    @Column(nullable = false)
    private String companyName;

    @Column(unique = true)
    private String siret;

    @Column(unique = true)
    private String tvaNumber;

    private String website;

    @Column(columnDefinition = "TEXT")
    private String companyDescription;

    private Boolean certified = false;

    private String certificationUrl;

    private String specialty;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id")
    private Plan plan;

    // Relations

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "professional_interests",
        joinColumns = @JoinColumn(name = "professional_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<Category> interests = new ArrayList<>();
}

