package com.purple_dog.mvp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserStatsDTO {
    // User counts
    private Long totalUsers;
    private Long activeUsers;
    private Long suspendedUsers;
    private Long individuals;
    private Long professionals;
    
    // Growth metrics
    private Long newUsersToday;
    private Long newUsersThisWeek;
    private Long newUsersThisMonth;
    private Double growthRate; // Month over month percentage
    
    // Engagement metrics
    private Long usersWithProducts;
    private Long usersWithOrders;
    private Double averageProductsPerUser;
    private Double averageOrdersPerUser;
    
    // Time series data
    private Map<LocalDate, Long> registrationsByDay; // Last 30 days
    private Map<String, Long> registrationsByMonth; // Last 12 months
    private Map<String, Long> usersByRole;
    private Map<String, Long> usersByStatus;
}
