package com.purple_dog.mvp.web;

import com.purple_dog.mvp.dto.*;
import com.purple_dog.mvp.services.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/dashboard")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Admin Dashboard", description = "Admin dashboard statistics and analytics")
@SecurityRequirement(name = "Bearer Authentication")
@PreAuthorize("hasRole('ADMIN')")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/stats")
    @Operation(summary = "Get overall dashboard statistics")
    public ResponseEntity<DashboardStatsDTO> getDashboardStats() {
        log.info("GET /api/admin/dashboard/stats - Getting dashboard statistics");
        DashboardStatsDTO stats = dashboardService.getDashboardStats();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/users/stats")
    @Operation(summary = "Get detailed user statistics")
    public ResponseEntity<UserStatsDTO> getUserStats() {
        log.info("GET /api/admin/dashboard/users/stats - Getting user statistics");
        UserStatsDTO stats = dashboardService.getUserStats();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/products/stats")
    @Operation(summary = "Get detailed product statistics")
    public ResponseEntity<ProductStatsDTO> getProductStats() {
        log.info("GET /api/admin/dashboard/products/stats - Getting product statistics");
        ProductStatsDTO stats = dashboardService.getProductStats();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/revenue/stats")
    @Operation(summary = "Get detailed revenue statistics")
    public ResponseEntity<RevenueStatsDTO> getRevenueStats() {
        log.info("GET /api/admin/dashboard/revenue/stats - Getting revenue statistics");
        RevenueStatsDTO stats = dashboardService.getRevenueStats();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/activity")
    @Operation(summary = "Get recent activity logs")
    public ResponseEntity<List<ActivityLogDTO>> getRecentActivity(
            @RequestParam(defaultValue = "20") int limit) {
        log.info("GET /api/admin/dashboard/activity - Getting recent activity (limit: {})", limit);
        List<ActivityLogDTO> activities = dashboardService.getRecentActivity(limit);
        return ResponseEntity.ok(activities);
    }

    @GetMapping("/users")
    @Operation(summary = "Get paginated list of all users for admin management")
    public ResponseEntity<Page<PersonResponseDTO>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {
        log.info("GET /api/admin/dashboard/users - Getting all users (page: {}, size: {}, sort: {} {})", 
                page, size, sortBy, direction);
        
        Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC") ? 
                Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        
        Page<PersonResponseDTO> users = dashboardService.getAllUsersForAdmin(pageable);
        return ResponseEntity.ok(users);
    }
}
