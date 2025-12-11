package com.purple_dog.mvp.services;

import com.purple_dog.mvp.dao.*;
import com.purple_dog.mvp.dto.*;
import com.purple_dog.mvp.entities.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DashboardService {

    private final PersonRepository personRepository;
    private final IndividualRepository individualRepository;
    private final ProfessionalRepository professionalRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final AuctionRepository auctionRepository;
    private final SupportTicketRepository supportTicketRepository;
    private final PlatformReviewRepository platformReviewRepository;
    private final PaymentRepository paymentRepository;

    /**
     * Get overall dashboard statistics
     */
    public DashboardStatsDTO getDashboardStats() {
        log.info("Fetching dashboard statistics");
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfMonth = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        
        // User statistics
        long totalUsers = personRepository.count();
        long totalIndividuals = individualRepository.count();
        long totalProfessionals = professionalRepository.count();
        long newUsersThisMonth = personRepository.countByCreatedAtAfter(startOfMonth);
        long activeUsers = personRepository.countByAccountStatus(AccountStatus.ACTIVE);
        
        // Product statistics
        long totalProducts = productRepository.count();
        long activeProducts = productRepository.count(); // TODO: add status filter
        long pendingProducts = 0L; // TODO: count by PENDING status
        long soldProducts = 0L; // TODO: count by SOLD status
        
        // Order statistics
        long totalOrders = orderRepository.count();
        long pendingOrders = 0L; // TODO: count by PENDING status
        long completedOrders = 0L; // TODO: count by COMPLETED status
        long canceledOrders = 0L; // TODO: count by CANCELED status
        
        BigDecimal totalRevenue = calculateTotalRevenue();
        BigDecimal revenueThisMonth = calculateRevenueForPeriod(startOfMonth, now);
        
        // Auction statistics
        long totalAuctions = auctionRepository.count();
        long activeAuctions = auctionRepository.countByStatus(AuctionStatus.ACTIVE);
        long completedAuctions = auctionRepository.countByStatus(AuctionStatus.ENDED);
        
        // Support statistics
        long totalTickets = supportTicketRepository.count();
        long openTickets = supportTicketRepository.countByStatus(TicketStatus.OPEN);
        long resolvedTickets = supportTicketRepository.countByStatus(TicketStatus.RESOLVED);
        
        // Platform reviews
        long totalReviews = platformReviewRepository.count();
        Double averageRating = calculateAverageRating();
        
        // Time series data
        Map<String, Long> userRegistrationsByMonth = getUserRegistrationsByMonth(12);
        Map<String, BigDecimal> revenueByMonth = getRevenueByMonth(12);
        Map<String, Long> ordersByStatus = getOrdersByStatus();
        
        return DashboardStatsDTO.builder()
                .totalUsers(totalUsers)
                .totalIndividuals(totalIndividuals)
                .totalProfessionals(totalProfessionals)
                .newUsersThisMonth(newUsersThisMonth)
                .activeUsers(activeUsers)
                .totalProducts(totalProducts)
                .activeProducts(activeProducts)
                .pendingProducts(pendingProducts)
                .soldProducts(soldProducts)
                .totalOrders(totalOrders)
                .pendingOrders(pendingOrders)
                .completedOrders(completedOrders)
                .canceledOrders(canceledOrders)
                .totalRevenue(totalRevenue)
                .revenueThisMonth(revenueThisMonth)
                .totalAuctions(totalAuctions)
                .activeAuctions(activeAuctions)
                .completedAuctions(completedAuctions)
                .totalTickets(totalTickets)
                .openTickets(openTickets)
                .resolvedTickets(resolvedTickets)
                .totalReviews(totalReviews)
                .averageRating(averageRating)
                .userRegistrationsByMonth(userRegistrationsByMonth)
                .revenueByMonth(revenueByMonth)
                .ordersByStatus(ordersByStatus)
                .build();
    }

    /**
     * Get detailed user statistics
     */
    public UserStatsDTO getUserStats() {
        log.info("Fetching user statistics");
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfToday = now.toLocalDate().atStartOfDay();
        LocalDateTime startOfWeek = now.minusWeeks(1);
        LocalDateTime startOfMonth = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime startOfLastMonth = startOfMonth.minusMonths(1);
        
        long totalUsers = personRepository.count();
        long activeUsers = personRepository.countByAccountStatus(AccountStatus.ACTIVE);
        long suspendedUsers = personRepository.countByAccountStatus(AccountStatus.SUSPENDED);
        long individuals = individualRepository.count();
        long professionals = professionalRepository.count();
        
        long newUsersToday = personRepository.countByCreatedAtAfter(startOfToday);
        long newUsersThisWeek = personRepository.countByCreatedAtAfter(startOfWeek);
        long newUsersThisMonth = personRepository.countByCreatedAtAfter(startOfMonth);
        long newUsersLastMonth = personRepository.countByCreatedAtBetween(startOfLastMonth, startOfMonth);
        
        double growthRate = calculateGrowthRate(newUsersThisMonth, newUsersLastMonth);
        
        long usersWithProducts = 0L; // TODO: implement
        long usersWithOrders = 0L; // TODO: implement
        double averageProductsPerUser = totalUsers > 0 ? (double) productRepository.count() / totalUsers : 0.0;
        double averageOrdersPerUser = totalUsers > 0 ? (double) orderRepository.count() / totalUsers : 0.0;
        
        Map<LocalDate, Long> registrationsByDay = getRegistrationsByDay(30);
        Map<String, Long> registrationsByMonth = getUserRegistrationsByMonth(12);
        Map<String, Long> usersByRole = getUsersByRole();
        Map<String, Long> usersByStatus = getUsersByStatus();
        
        return UserStatsDTO.builder()
                .totalUsers(totalUsers)
                .activeUsers(activeUsers)
                .suspendedUsers(suspendedUsers)
                .individuals(individuals)
                .professionals(professionals)
                .newUsersToday(newUsersToday)
                .newUsersThisWeek(newUsersThisWeek)
                .newUsersThisMonth(newUsersThisMonth)
                .growthRate(growthRate)
                .usersWithProducts(usersWithProducts)
                .usersWithOrders(usersWithOrders)
                .averageProductsPerUser(averageProductsPerUser)
                .averageOrdersPerUser(averageOrdersPerUser)
                .registrationsByDay(registrationsByDay)
                .registrationsByMonth(registrationsByMonth)
                .usersByRole(usersByRole)
                .usersByStatus(usersByStatus)
                .build();
    }

    /**
     * Get detailed product statistics
     */
    public ProductStatsDTO getProductStats() {
        log.info("Fetching product statistics");
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfToday = now.toLocalDate().atStartOfDay();
        LocalDateTime startOfWeek = now.minusWeeks(1);
        LocalDateTime startOfMonth = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        
        long totalProducts = productRepository.count();
        long activeProducts = totalProducts; // TODO: filter by status
        long pendingProducts = 0L;
        long soldProducts = 0L;
        long rejectedProducts = 0L;
        
        long newProductsToday = 0L; // TODO: count by created date
        long newProductsThisWeek = 0L;
        long newProductsThisMonth = 0L;
        
        Map<String, Long> productsByCategory = getProductsByCategory();
        Map<String, Long> productsByStatus = new HashMap<>(); // TODO: implement
        Map<String, Long> productsByCondition = new HashMap<>(); // TODO: implement
        
        long productsFromIndividuals = 0L; // TODO: implement
        long productsFromProfessionals = 0L; // TODO: implement
        
        List<Product> allProducts = productRepository.findAll();
        double averageProductPrice = allProducts.stream()
                .map(Product::getEstimatedValue)
                .filter(Objects::nonNull)
                .mapToDouble(BigDecimal::doubleValue)
                .average()
                .orElse(0.0);
        
        BigDecimal lowestPrice = allProducts.stream()
                .map(Product::getEstimatedValue)
                .filter(Objects::nonNull)
                .min(Comparator.naturalOrder())
                .orElse(BigDecimal.ZERO);
        
        BigDecimal highestPrice = allProducts.stream()
                .map(Product::getEstimatedValue)
                .filter(Objects::nonNull)
                .max(Comparator.naturalOrder())
                .orElse(BigDecimal.ZERO);
        
        BigDecimal medianPrice = calculateMedianPrice(allProducts);
        
        return ProductStatsDTO.builder()
                .totalProducts(totalProducts)
                .activeProducts(activeProducts)
                .pendingProducts(pendingProducts)
                .soldProducts(soldProducts)
                .rejectedProducts(rejectedProducts)
                .newProductsToday(newProductsToday)
                .newProductsThisWeek(newProductsThisWeek)
                .newProductsThisMonth(newProductsThisMonth)
                .productsByCategory(productsByCategory)
                .productsByStatus(productsByStatus)
                .productsByCondition(productsByCondition)
                .productsFromIndividuals(productsFromIndividuals)
                .productsFromProfessionals(productsFromProfessionals)
                .averageProductPrice(averageProductPrice)
                .totalViews(0L)
                .totalFavorites(0L)
                .conversionRate(0.0)
                .lowestPrice(lowestPrice)
                .highestPrice(highestPrice)
                .medianPrice(medianPrice)
                .build();
    }

    /**
     * Get detailed revenue statistics
     */
    public RevenueStatsDTO getRevenueStats() {
        log.info("Fetching revenue statistics");
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfToday = now.toLocalDate().atStartOfDay();
        LocalDateTime startOfWeek = now.minusWeeks(1);
        LocalDateTime startOfMonth = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime startOfYear = now.withDayOfYear(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime startOfLastMonth = startOfMonth.minusMonths(1);
        LocalDateTime endOfLastMonth = startOfMonth.minusSeconds(1);
        LocalDateTime startOfLastYear = startOfYear.minusYears(1);
        LocalDateTime endOfLastYear = startOfYear.minusSeconds(1);
        
        BigDecimal totalRevenue = calculateTotalRevenue();
        BigDecimal revenueToday = calculateRevenueForPeriod(startOfToday, now);
        BigDecimal revenueThisWeek = calculateRevenueForPeriod(startOfWeek, now);
        BigDecimal revenueThisMonth = calculateRevenueForPeriod(startOfMonth, now);
        BigDecimal revenueThisYear = calculateRevenueForPeriod(startOfYear, now);
        
        BigDecimal revenueLastMonth = calculateRevenueForPeriod(startOfLastMonth, endOfLastMonth);
        double monthOverMonthGrowth = calculateRevenueGrowth(revenueThisMonth, revenueLastMonth);
        
        BigDecimal revenueLastYear = calculateRevenueForPeriod(startOfLastYear, endOfLastYear);
        double yearOverYearGrowth = calculateRevenueGrowth(revenueThisYear, revenueLastYear);
        
        long totalOrders = orderRepository.count();
        BigDecimal averageOrderValue = totalOrders > 0 ? 
                totalRevenue.divide(BigDecimal.valueOf(totalOrders), 2, RoundingMode.HALF_UP) : 
                BigDecimal.ZERO;
        
        List<Order> allOrders = orderRepository.findAll();
        BigDecimal highestOrderValue = allOrders.stream()
                .map(Order::getTotalAmount)
                .filter(Objects::nonNull)
                .max(Comparator.naturalOrder())
                .orElse(BigDecimal.ZERO);
        
        BigDecimal lowestOrderValue = allOrders.stream()
                .map(Order::getTotalAmount)
                .filter(Objects::nonNull)
                .min(Comparator.naturalOrder())
                .orElse(BigDecimal.ZERO);
        
        Map<LocalDate, BigDecimal> revenueByDay = getRevenueByDay(30);
        Map<String, BigDecimal> revenueByMonth = getRevenueByMonth(12);
        Map<String, BigDecimal> revenueByYear = getRevenueByYear();
        Map<String, BigDecimal> revenueByCategory = getRevenueByCategory();
        
        return RevenueStatsDTO.builder()
                .totalRevenue(totalRevenue)
                .revenueToday(revenueToday)
                .revenueThisWeek(revenueThisWeek)
                .revenueThisMonth(revenueThisMonth)
                .revenueThisYear(revenueThisYear)
                .revenueLastMonth(revenueLastMonth)
                .monthOverMonthGrowth(monthOverMonthGrowth)
                .revenueLastYear(revenueLastYear)
                .yearOverYearGrowth(yearOverYearGrowth)
                .totalOrders(totalOrders)
                .averageOrderValue(averageOrderValue)
                .highestOrderValue(highestOrderValue)
                .lowestOrderValue(lowestOrderValue)
                .revenueByDay(revenueByDay)
                .revenueByMonth(revenueByMonth)
                .revenueByYear(revenueByYear)
                .revenueByCategory(revenueByCategory)
                .pendingRevenue(BigDecimal.ZERO)
                .completedRevenue(totalRevenue)
                .refundedRevenue(BigDecimal.ZERO)
                .build();
    }

    /**
     * Get recent activity logs
     */
    public List<ActivityLogDTO> getRecentActivity(int limit) {
        log.info("Fetching recent activity (limit: {})", limit);
        
        List<ActivityLogDTO> activities = new ArrayList<>();
        
        // Get recent user registrations
        List<Person> recentUsers = personRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(Person::getCreatedAt).reversed())
                .limit(limit / 4)
                .collect(Collectors.toList());
        
        for (Person user : recentUsers) {
            activities.add(ActivityLogDTO.builder()
                    .activityType("USER_REGISTERED")
                    .description(user.getFirstName() + " " + user.getLastName() + " s'est inscrit")
                    .userEmail(user.getEmail())
                    .userName(user.getFirstName() + " " + user.getLastName())
                    .userId(user.getId())
                    .entityType("USER")
                    .entityId(user.getId())
                    .timestamp(user.getCreatedAt())
                    .severity("INFO")
                    .build());
        }
        
        // Get recent products
        List<Product> recentProducts = productRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(Product::getCreatedAt).reversed())
                .limit(limit / 4)
                .collect(Collectors.toList());
        
        for (Product product : recentProducts) {
            activities.add(ActivityLogDTO.builder()
                    .activityType("PRODUCT_LISTED")
                    .description("Nouveau produit: " + product.getTitle())
                    .entityType("PRODUCT")
                    .entityId(product.getId())
                    .timestamp(product.getCreatedAt())
                    .severity("INFO")
                    .build());
        }
        
        // Get recent orders
        List<Order> recentOrders = orderRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(Order::getCreatedAt).reversed())
                .limit(limit / 4)
                .collect(Collectors.toList());
        
        for (Order order : recentOrders) {
            activities.add(ActivityLogDTO.builder()
                    .activityType("ORDER_PLACED")
                    .description("Nouvelle commande #" + order.getId())
                    .entityType("ORDER")
                    .entityId(order.getId())
                    .timestamp(order.getCreatedAt())
                    .severity("INFO")
                    .build());
        }
        
        // Get recent support tickets
        List<SupportTicket> recentTickets = supportTicketRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(SupportTicket::getCreatedAt).reversed())
                .limit(limit / 4)
                .collect(Collectors.toList());
        
        for (SupportTicket ticket : recentTickets) {
            activities.add(ActivityLogDTO.builder()
                    .activityType("TICKET_CREATED")
                    .description("Nouveau ticket: " + ticket.getSubject())
                    .entityType("TICKET")
                    .entityId(ticket.getId())
                    .timestamp(ticket.getCreatedAt())
                    .severity(ticket.getPriority() == TicketPriority.HIGH ? "CRITICAL" : "INFO")
                    .build());
        }
        
        // Sort all activities by timestamp and limit
        return activities.stream()
                .sorted(Comparator.comparing(ActivityLogDTO::getTimestamp).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Get paginated list of all users for admin management
     */
    public Page<PersonResponseDTO> getAllUsersForAdmin(Pageable pageable) {
        log.info("Fetching all users for admin (page: {}, size: {})", 
                pageable.getPageNumber(), pageable.getPageSize());
        return personRepository.findAll(pageable)
                .map(this::mapToPersonResponseDTO);
    }

    // Helper methods

    private BigDecimal calculateTotalRevenue() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream()
                .map(Order::getTotalAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateRevenueForPeriod(LocalDateTime start, LocalDateTime end) {
        List<Order> orders = orderRepository.findAll().stream()
                .filter(o -> o.getCreatedAt() != null)
                .filter(o -> !o.getCreatedAt().isBefore(start) && !o.getCreatedAt().isAfter(end))
                .collect(Collectors.toList());
        
        return orders.stream()
                .map(Order::getTotalAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Double calculateAverageRating() {
        List<PlatformReview> reviews = platformReviewRepository.findAll();
        return reviews.stream()
                .map(PlatformReview::getRating)
                .filter(Objects::nonNull)
                .mapToDouble(Integer::doubleValue)
                .average()
                .orElse(0.0);
    }

    private Map<String, Long> getUserRegistrationsByMonth(int months) {
        LocalDateTime now = LocalDateTime.now();
        Map<String, Long> result = new LinkedHashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        
        for (int i = months - 1; i >= 0; i--) {
            YearMonth yearMonth = YearMonth.from(now.minusMonths(i));
            LocalDateTime start = yearMonth.atDay(1).atStartOfDay();
            LocalDateTime end = yearMonth.atEndOfMonth().atTime(23, 59, 59);
            
            long count = personRepository.findAll().stream()
                    .filter(p -> p.getCreatedAt() != null)
                    .filter(p -> !p.getCreatedAt().isBefore(start) && !p.getCreatedAt().isAfter(end))
                    .count();
            
            result.put(yearMonth.format(formatter), count);
        }
        
        return result;
    }

    private Map<String, BigDecimal> getRevenueByMonth(int months) {
        LocalDateTime now = LocalDateTime.now();
        Map<String, BigDecimal> result = new LinkedHashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        
        for (int i = months - 1; i >= 0; i--) {
            YearMonth yearMonth = YearMonth.from(now.minusMonths(i));
            LocalDateTime start = yearMonth.atDay(1).atStartOfDay();
            LocalDateTime end = yearMonth.atEndOfMonth().atTime(23, 59, 59);
            
            BigDecimal revenue = calculateRevenueForPeriod(start, end);
            result.put(yearMonth.format(formatter), revenue);
        }
        
        return result;
    }

    private Map<String, Long> getOrdersByStatus() {
        Map<String, Long> result = new HashMap<>();
        // TODO: Implement based on OrderStatus enum
        result.put("PENDING", 0L);
        result.put("COMPLETED", 0L);
        result.put("CANCELED", 0L);
        return result;
    }

    private Map<LocalDate, Long> getRegistrationsByDay(int days) {
        LocalDate today = LocalDate.now();
        Map<LocalDate, Long> result = new LinkedHashMap<>();
        
        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = date.atTime(23, 59, 59);
            
            long count = personRepository.findAll().stream()
                    .filter(p -> p.getCreatedAt() != null)
                    .filter(p -> !p.getCreatedAt().isBefore(start) && !p.getCreatedAt().isAfter(end))
                    .count();
            
            result.put(date, count);
        }
        
        return result;
    }

    private Map<String, Long> getUsersByRole() {
        Map<String, Long> result = new HashMap<>();
        result.put("INDIVIDUAL", individualRepository.count());
        result.put("PROFESSIONAL", professionalRepository.count());
        return result;
    }

    private Map<String, Long> getUsersByStatus() {
        Map<String, Long> result = new HashMap<>();
        result.put("ACTIVE", personRepository.countByAccountStatus(AccountStatus.ACTIVE));
        result.put("SUSPENDED", personRepository.countByAccountStatus(AccountStatus.SUSPENDED));
        result.put("PENDING", personRepository.countByAccountStatus(AccountStatus.PENDING_VERIFICATION));
        return result;
    }

    private Map<String, Long> getProductsByCategory() {
        Map<String, Long> result = new HashMap<>();
        List<Product> products = productRepository.findAll();
        
        products.stream()
                .filter(p -> p.getCategory() != null)
                .forEach(p -> {
                    String categoryName = p.getCategory().getName();
                    result.merge(categoryName, 1L, Long::sum);
                });
        
        return result;
    }

    private BigDecimal calculateMedianPrice(List<Product> products) {
        List<BigDecimal> prices = products.stream()
                .map(Product::getEstimatedValue)
                .filter(Objects::nonNull)
                .sorted()
                .collect(Collectors.toList());
        
        if (prices.isEmpty()) return BigDecimal.ZERO;
        
        int size = prices.size();
        if (size % 2 == 0) {
            return prices.get(size / 2 - 1)
                    .add(prices.get(size / 2))
                    .divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);
        } else {
            return prices.get(size / 2);
        }
    }

    private Map<LocalDate, BigDecimal> getRevenueByDay(int days) {
        LocalDate today = LocalDate.now();
        Map<LocalDate, BigDecimal> result = new LinkedHashMap<>();
        
        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = date.atTime(23, 59, 59);
            
            BigDecimal revenue = calculateRevenueForPeriod(start, end);
            result.put(date, revenue);
        }
        
        return result;
    }

    private Map<String, BigDecimal> getRevenueByYear() {
        Map<String, BigDecimal> result = new HashMap<>();
        // TODO: Implement based on available data
        return result;
    }

    private Map<String, BigDecimal> getRevenueByCategory() {
        Map<String, BigDecimal> result = new HashMap<>();
        List<Order> orders = orderRepository.findAll();
        
        orders.forEach(order -> {
            Product product = null;
            // Get product from auction or quickSale
            if (order.getAuction() != null && order.getAuction().getProduct() != null) {
                product = order.getAuction().getProduct();
            } else if (order.getQuickSale() != null && order.getQuickSale().getProduct() != null) {
                product = order.getQuickSale().getProduct();
            }
            
            if (product != null && product.getCategory() != null) {
                String category = product.getCategory().getName();
                result.merge(category, order.getTotalAmount(), BigDecimal::add);
            }
        });
        
        return result;
    }

    private double calculateGrowthRate(long current, long previous) {
        if (previous == 0) return current > 0 ? 100.0 : 0.0;
        return ((double) (current - previous) / previous) * 100.0;
    }

    private double calculateRevenueGrowth(BigDecimal current, BigDecimal previous) {
        if (previous.compareTo(BigDecimal.ZERO) == 0) {
            return current.compareTo(BigDecimal.ZERO) > 0 ? 100.0 : 0.0;
        }
        return current.subtract(previous)
                .divide(previous, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();
    }

    private PersonResponseDTO mapToPersonResponseDTO(Person person) {
        PersonResponseDTO dto = new PersonResponseDTO();
        dto.setId(person.getId());
        dto.setEmail(person.getEmail());
        dto.setFirstName(person.getFirstName());
        dto.setLastName(person.getLastName());
        dto.setPhone(person.getPhone());
        dto.setRole(person.getRole());
        dto.setAccountStatus(person.getAccountStatus());
        dto.setProfilePicture(person.getProfilePicture());
        dto.setBio(person.getBio());
        dto.setEmailVerified(person.getEmailVerified());
        dto.setPhoneVerified(person.getPhoneVerified());
        dto.setCreatedAt(person.getCreatedAt());
        dto.setUpdatedAt(person.getUpdatedAt());
        dto.setLastLoginAt(person.getLastLoginAt());
        return dto;
    }
}
