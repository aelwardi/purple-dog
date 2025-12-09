package com.purple_dog.mvp.dto;

import com.purple_dog.mvp.entities.ProductCondition;
import com.purple_dog.mvp.entities.SaleType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductCreateRequest {
    private Long sellerId;
    private Long categoryId;
    private String title;
    private String description;
    private ProductCondition productCondition;
    private SaleType saleType;
    private BigDecimal estimatedValue;
    private String brand;
    private Integer yearOfManufacture;
    private String origin;
    private String authenticityCertificate;
    private Boolean hasDocumentation;
    private BigDecimal widthCm;
    private BigDecimal heightCm;
    private BigDecimal depthCm;
    private BigDecimal weightKg;
    private List<String> photoUrls;
    private List<DocumentUploadRequest> documents;
}
