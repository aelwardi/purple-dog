package com.purple_dog.mvp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RevenueStatsDTO {
    // Total revenue metrics
    private BigDecimal totalRevenue;
    private BigDecimal revenueToday;
    private BigDecimal revenueThisWeek;
    private BigDecimal revenueThisMonth;
    private BigDecimal revenueThisYear;
    
    // Growth metrics
    private BigDecimal revenueLastMonth;
    private Double monthOverMonthGrowth;
    private BigDecimal revenueLastYear;
    private Double yearOverYearGrowth;
    
    // Order metrics
    private Long totalOrders;
    private BigDecimal averageOrderValue;
    private BigDecimal highestOrderValue;
    private BigDecimal lowestOrderValue;
    
    // Time series data
    private Map<LocalDate, BigDecimal> revenueByDay; // Last 30 days
    private Map<String, BigDecimal> revenueByMonth; // Last 12 months
    private Map<String, BigDecimal> revenueByYear;
    
    // Category breakdown
    private Map<String, BigDecimal> revenueByCategory;
    
    // Payment status
    private BigDecimal pendingRevenue;
    private BigDecimal completedRevenue;
    private BigDecimal refundedRevenue;
}
