package com.purple_dog.mvp.web.rest;

import com.purple_dog.mvp.dto.*;
import com.purple_dog.mvp.services.ShippoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/shippo")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Shippo", description = "Shipping management with Shippo")
@SecurityRequirement(name = "bearerAuth")
public class ShippoController {

    private final ShippoService shippoService;

    /**
     * Create a shipment and get shipping rates
     * POST /api/shippo/create-shipment
     */
    @PostMapping("/create-shipment")
    @PreAuthorize("hasAnyRole('INDIVIDUAL', 'PROFESSIONAL', 'ADMIN')")
    @Operation(summary = "Create shipment and get rates", description = "Create a Shippo shipment and retrieve available shipping rates")
    public ResponseEntity<ShippingRatesDTO> createShipmentAndGetRates(@Valid @RequestBody CreateShipmentDTO request) {
        log.info("Creating shipment for order: {}", request.getOrderId());
        ShippingRatesDTO response = shippoService.createShipmentAndGetRates(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Purchase a shipping label
     * POST /api/shippo/deliveries/{deliveryId}/purchase-label
     */
    @PostMapping("/deliveries/{deliveryId}/purchase-label")
    @PreAuthorize("hasAnyRole('INDIVIDUAL', 'PROFESSIONAL', 'ADMIN')")
    @Operation(summary = "Purchase shipping label", description = "Purchase a shipping label for a delivery")
    public ResponseEntity<DeliveryResponseDTO> purchaseLabel(
            @PathVariable Long deliveryId,
            @Valid @RequestBody PurchaseLabelDTO request) {
        log.info("Purchasing label for delivery: {}", deliveryId);
        DeliveryResponseDTO response = shippoService.purchaseLabel(deliveryId, request);
        return ResponseEntity.ok(response);
    }
}

