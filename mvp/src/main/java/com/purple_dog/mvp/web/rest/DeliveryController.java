package com.purple_dog.mvp.web.rest;

import com.purple_dog.mvp.dto.*;
import com.purple_dog.mvp.services.ShippoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/deliveries")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Delivery", description = "Delivery and shipping management with Shippo")
@SecurityRequirement(name = "bearerAuth")
public class DeliveryController {

    private final ShippoService shippoService;

    /**
     * Create shipment and get shipping rates
     * POST /api/deliveries/shipment
     */
    @PostMapping("/shipment")
    @PreAuthorize("hasAnyRole('PROFESSIONAL', 'ADMIN')")
    @Operation(summary = "Create shipment", description = "Create a shipment and get available shipping rates")
    public ResponseEntity<ShippingRatesDTO> createShipment(@Valid @RequestBody CreateShipmentDTO request) {
        log.info("Creating shipment for order: {}", request.getOrderId());
        ShippingRatesDTO response = shippoService.createShipmentAndGetRates(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Purchase a shipping label
     * POST /api/deliveries/{id}/label
     */
    @PostMapping("/{id}/label")
    @PreAuthorize("hasAnyRole('PROFESSIONAL', 'ADMIN')")
    @Operation(summary = "Purchase label", description = "Purchase a shipping label for a delivery")
    public ResponseEntity<DeliveryResponseDTO> purchaseLabel(
            @PathVariable Long id,
            @Valid @RequestBody PurchaseLabelDTO request) {
        log.info("Purchasing label for delivery: {}", id);
        DeliveryResponseDTO response = shippoService.purchaseLabel(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Get tracking information
     * GET /api/deliveries/{id}/tracking
     */
    @GetMapping("/{id}/tracking")
    @PreAuthorize("hasAnyRole('INDIVIDUAL', 'PROFESSIONAL', 'ADMIN')")
    @Operation(summary = "Get tracking info", description = "Get tracking information for a delivery")
    public ResponseEntity<DeliveryResponseDTO> getTrackingInfo(@PathVariable Long id) {
        log.info("Getting tracking info for delivery: {}", id);
        DeliveryResponseDTO response = shippoService.getTrackingInfo(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Get delivery by ID
     * GET /api/deliveries/{id}
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('INDIVIDUAL', 'PROFESSIONAL', 'ADMIN')")
    @Operation(summary = "Get delivery", description = "Get delivery details by ID")
    public ResponseEntity<DeliveryResponseDTO> getDelivery(@PathVariable Long id) {
        log.info("Getting delivery: {}", id);
        DeliveryResponseDTO response = shippoService.getDelivery(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Get deliveries for an order
     * GET /api/deliveries/order/{orderId}
     */
    @GetMapping("/order/{orderId}")
    @PreAuthorize("hasAnyRole('INDIVIDUAL', 'PROFESSIONAL', 'ADMIN')")
    @Operation(summary = "Get order deliveries", description = "Get all deliveries for an order")
    public ResponseEntity<List<DeliveryResponseDTO>> getOrderDeliveries(@PathVariable Long orderId) {
        log.info("Getting deliveries for order: {}", orderId);
        List<DeliveryResponseDTO> response = shippoService.getOrderDeliveries(orderId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get all deliveries (Admin)
     * GET /api/deliveries
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all deliveries", description = "Get all deliveries with pagination (Admin only)")
    public ResponseEntity<Page<DeliveryResponseDTO>> getAllDeliveries(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        log.info("Getting all deliveries");
        Page<DeliveryResponseDTO> response = shippoService.getAllDeliveries(pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Validate an address
     * POST /api/deliveries/validate-address
     */
    @PostMapping("/validate-address")
    @PreAuthorize("hasAnyRole('PROFESSIONAL', 'ADMIN')")
    @Operation(summary = "Validate address", description = "Validate a shipping address using Shippo")
    public ResponseEntity<Map<String, Object>> validateAddress(@RequestBody Map<String, Object> addressMap) {
        log.info("Validating address");
        Map<String, Object> response = shippoService.validateAddress(addressMap);
        return ResponseEntity.ok(response);
    }
}

