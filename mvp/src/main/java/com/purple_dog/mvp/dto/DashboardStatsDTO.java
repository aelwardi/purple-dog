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
public class DashboardStatsDTO {
    // User Statistics
    private Long totalUsers;
    private Long totalIndividuals;
    private Long totalProfessionals;
    private Long newUsersThisMonth;
    private Long activeUsers;
    
    // Product Statistics
    private Long totalProducts;
    private Long activeProducts;
    private Long pendingProducts;
    private Long soldProducts;
    
    // Order Statistics
    private Long totalOrders;
    private Long pendingOrders;
    private Long completedOrders;
    private Long canceledOrders;
    private BigDecimal totalRevenue;
    private BigDecimal revenueThisMonth;
    
    // Auction Statistics
    private Long totalAuctions;
    private Long activeAuctions;
    private Long completedAuctions;
    
    // Support Statistics
    private Long totalTickets;
    private Long openTickets;
    private Long resolvedTickets;
    
    // Platform Reviews
    private Long totalReviews;
    private Double averageRating;
    
    // Recent Activity
    private Map<String, Long> userRegistrationsByMonth;
    private Map<String, BigDecimal> revenueByMonth;
    private Map<String, Long> ordersByStatus;
}
