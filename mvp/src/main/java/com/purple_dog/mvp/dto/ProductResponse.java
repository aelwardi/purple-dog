package com.purple_dog.mvp.dto;

import com.purple_dog.mvp.entities.ProductCondition;
import com.purple_dog.mvp.entities.ProductStatus;
import com.purple_dog.mvp.entities.SaleType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Long id;
    private Long sellerId;
    private Long categoryId;
    private String title;
    private String description;
    private ProductCondition productCondition;
    private ProductStatus status;
    private SaleType saleType;
    private BigDecimal price;  // Prix du produit (alias pour estimatedValue pour compatibilité frontend)
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
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<PhotoResponse> photos;
    private List<DocumentResponseDTO> documents;
    private SellerInfo seller;
    private CategoryInfo category;
    private Long quickSaleId;
    private Long auctionId;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SellerInfo {
        private Long id;
        private String firstName;
        private String lastName;
        private String email;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryInfo {
        private Long id;
        private String name;
        private String description;
    }
}
