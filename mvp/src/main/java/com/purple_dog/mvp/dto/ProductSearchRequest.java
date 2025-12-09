package com.purple_dog.mvp.dto;

import com.purple_dog.mvp.entities.ProductStatus;
import com.purple_dog.mvp.entities.SaleType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductSearchRequest {
    private String text;
    private Long categoryId;
    private SaleType saleType;
    private ProductStatus status;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Boolean availableOnly;
}
