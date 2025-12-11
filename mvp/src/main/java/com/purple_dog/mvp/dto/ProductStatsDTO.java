package com.purple_dog.mvp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductStatsDTO {
    // Product counts
    private Long totalProducts;
    private Long activeProducts;
    private Long pendingProducts;
    private Long soldProducts;
    private Long rejectedProducts;
    
    // Listing metrics
    private Long newProductsToday;
    private Long newProductsThisWeek;
    private Long newProductsThisMonth;
    
    // Category distribution
    private Map<String, Long> productsByCategory;
    private Map<String, Long> productsByStatus;
    private Map<String, Long> productsByCondition;
    
    // Seller metrics
    private Long productsFromIndividuals;
    private Long productsFromProfessionals;
    private Double averageProductPrice;
    
    // Sales performance
    private Long totalViews;
    private Long totalFavorites;
    private Double conversionRate; // sold / total
    
    // Price analytics
    private BigDecimal lowestPrice;
    private BigDecimal highestPrice;
    private BigDecimal medianPrice;
}
