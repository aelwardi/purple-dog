package com.purple_dog.mvp.web;

import com.purple_dog.mvp.dto.DeliveryCreateDTO;
import com.purple_dog.mvp.dto.DeliveryResponseDTO;
import com.purple_dog.mvp.dto.DeliveryUpdateDTO;
import com.purple_dog.mvp.entities.DeliveryStatus;
import com.purple_dog.mvp.services.DeliveryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/deliveries")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class DeliveryController {

    private final DeliveryService deliveryService;

    /**
     * Créer une nouvelle livraison
     */
    @PostMapping
    public ResponseEntity<DeliveryResponseDTO> createDelivery(@Valid @RequestBody DeliveryCreateDTO dto) {
        log.info("Request to create delivery for order: {}", dto.getOrderId());
        DeliveryResponseDTO response = deliveryService.createDelivery(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Récupérer toutes les livraisons
     */
    @GetMapping
    public ResponseEntity<List<DeliveryResponseDTO>> getAllDeliveries() {
        log.info("Request to get all deliveries");
        List<DeliveryResponseDTO> deliveries = deliveryService.getAllDeliveries();
        return ResponseEntity.ok(deliveries);
    }

    /**
     * Récupérer une livraison par ID
     */
    @GetMapping("/{deliveryId}")
    public ResponseEntity<DeliveryResponseDTO> getDeliveryById(@PathVariable Long deliveryId) {
        log.info("Request to get delivery: {}", deliveryId);
        DeliveryResponseDTO delivery = deliveryService.getDeliveryById(deliveryId);
        return ResponseEntity.ok(delivery);
    }

    /**
     * Récupérer une livraison par numéro de suivi
     */
    @GetMapping("/tracking/{trackingNumber}")
    public ResponseEntity<DeliveryResponseDTO> getDeliveryByTrackingNumber(@PathVariable String trackingNumber) {
        log.info("Request to get delivery by tracking number: {}", trackingNumber);
        DeliveryResponseDTO delivery = deliveryService.getDeliveryByTrackingNumber(trackingNumber);
        return ResponseEntity.ok(delivery);
    }

    /**
     * Récupérer la livraison d'une commande
     */
    @GetMapping("/order/{orderId}")
    public ResponseEntity<DeliveryResponseDTO> getDeliveryByOrderId(@PathVariable Long orderId) {
        log.info("Request to get delivery for order: {}", orderId);
        DeliveryResponseDTO delivery = deliveryService.getDeliveryByOrderId(orderId);
        return ResponseEntity.ok(delivery);
    }

    /**
     * Récupérer les livraisons d'un acheteur
     */
    @GetMapping("/buyer/{buyerId}")
    public ResponseEntity<List<DeliveryResponseDTO>> getDeliveriesByBuyerId(@PathVariable Long buyerId) {
        log.info("Request to get deliveries for buyer: {}", buyerId);
        List<DeliveryResponseDTO> deliveries = deliveryService.getDeliveriesByBuyerId(buyerId);
        return ResponseEntity.ok(deliveries);
    }

    /**
     * Récupérer les livraisons d'un vendeur
     */
    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<List<DeliveryResponseDTO>> getDeliveriesBySellerId(@PathVariable Long sellerId) {
        log.info("Request to get deliveries for seller: {}", sellerId);
        List<DeliveryResponseDTO> deliveries = deliveryService.getDeliveriesBySellerId(sellerId);
        return ResponseEntity.ok(deliveries);
    }

    /**
     * Récupérer les livraisons par statut
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<DeliveryResponseDTO>> getDeliveriesByStatus(@PathVariable DeliveryStatus status) {
        log.info("Request to get deliveries with status: {}", status);
        List<DeliveryResponseDTO> deliveries = deliveryService.getDeliveriesByStatus(status);
        return ResponseEntity.ok(deliveries);
    }

    /**
     * Récupérer les livraisons d'un transporteur
     */
    @GetMapping("/carrier/{carrierId}")
    public ResponseEntity<List<DeliveryResponseDTO>> getDeliveriesByCarrierId(@PathVariable Long carrierId) {
        log.info("Request to get deliveries for carrier: {}", carrierId);
        List<DeliveryResponseDTO> deliveries = deliveryService.getDeliveriesByCarrierId(carrierId);
        return ResponseEntity.ok(deliveries);
    }

    /**
     * Récupérer les livraisons en retard
     */
    @GetMapping("/delayed")
    public ResponseEntity<List<DeliveryResponseDTO>> getDelayedDeliveries() {
        log.info("Request to get delayed deliveries");
        List<DeliveryResponseDTO> deliveries = deliveryService.getDelayedDeliveries();
        return ResponseEntity.ok(deliveries);
    }

    /**
     * Mettre à jour une livraison
     */
    @PutMapping("/{deliveryId}")
    public ResponseEntity<DeliveryResponseDTO> updateDelivery(
            @PathVariable Long deliveryId,
            @Valid @RequestBody DeliveryUpdateDTO dto) {

        log.info("Request to update delivery: {}", deliveryId);
        DeliveryResponseDTO response = deliveryService.updateDelivery(deliveryId, dto);
        return ResponseEntity.ok(response);
    }

    /**
     * Mettre à jour le statut d'une livraison
     */
    @PatchMapping("/{deliveryId}/status/{status}")
    public ResponseEntity<DeliveryResponseDTO> updateDeliveryStatus(
            @PathVariable Long deliveryId,
            @PathVariable DeliveryStatus status) {

        log.info("Request to update delivery {} status to: {}", deliveryId, status);
        DeliveryResponseDTO response = deliveryService.updateDeliveryStatus(deliveryId, status);
        return ResponseEntity.ok(response);
    }

    /**
     * Générer l'étiquette de livraison
     */
    @PostMapping("/{deliveryId}/generate-label")
    public ResponseEntity<DeliveryResponseDTO> generateLabel(@PathVariable Long deliveryId) {
        log.info("Request to generate label for delivery: {}", deliveryId);
        DeliveryResponseDTO response = deliveryService.generateLabel(deliveryId);
        return ResponseEntity.ok(response);
    }

    /**
     * Marquer comme expédiée
     */
    @PatchMapping("/{deliveryId}/mark-shipped")
    public ResponseEntity<DeliveryResponseDTO> markAsShipped(@PathVariable Long deliveryId) {
        log.info("Request to mark delivery as shipped: {}", deliveryId);
        DeliveryResponseDTO response = deliveryService.markAsShipped(deliveryId);
        return ResponseEntity.ok(response);
    }

    /**
     * Marquer comme livrée
     */
    @PatchMapping("/{deliveryId}/mark-delivered")
    public ResponseEntity<DeliveryResponseDTO> markAsDelivered(@PathVariable Long deliveryId) {
        log.info("Request to mark delivery as delivered: {}", deliveryId);
        DeliveryResponseDTO response = deliveryService.markAsDelivered(deliveryId);
        return ResponseEntity.ok(response);
    }

    /**
     * Supprimer une livraison
     */
    @DeleteMapping("/{deliveryId}")
    public ResponseEntity<Void> deleteDelivery(@PathVariable Long deliveryId) {
        log.info("Request to delete delivery: {}", deliveryId);
        deliveryService.deleteDelivery(deliveryId);
        return ResponseEntity.noContent().build();
    }
}

