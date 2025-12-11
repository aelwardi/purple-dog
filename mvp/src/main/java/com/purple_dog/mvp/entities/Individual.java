package com.purple_dog.mvp.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "individuals")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Individual extends Person {

    @Column(name = "identity_verified")
    private Boolean identityVerified = false;

    @Column(name = "identity_document_url")
    private String identityDocumentUrl;

    @Column(name = "max_sales_per_month")
    private Integer maxSalesPerMonth = 10;
}

