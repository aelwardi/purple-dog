package com.purple_dog.mvp.services;

import com.purple_dog.mvp.dao.CarrierRepository;
import com.purple_dog.mvp.dao.DeliveryRepository;
import com.purple_dog.mvp.dao.OrderRepository;
import com.purple_dog.mvp.dto.*;
import com.purple_dog.mvp.entities.*;
import com.purple_dog.mvp.exceptions.InvalidOperationException;
import com.purple_dog.mvp.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final OrderRepository orderRepository;
    private final CarrierRepository carrierRepository;

    /**
     * Créer une nouvelle livraison
     */
    public DeliveryResponseDTO createDelivery(DeliveryCreateDTO dto) {
        log.info("Creating delivery for order: {}", dto.getOrderId());

        Order order = orderRepository.findById(dto.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + dto.getOrderId()));

        if (deliveryRepository.findByOrderId(dto.getOrderId()).isPresent()) {
            throw new InvalidOperationException("Delivery already exists for order: " + dto.getOrderId());
        }

        Carrier carrier = carrierRepository.findById(dto.getCarrierId())
                .orElseThrow(() -> new ResourceNotFoundException("Carrier not found with id: " + dto.getCarrierId()));

        if (!carrier.getActive()) {
            throw new InvalidOperationException("Carrier is not active: " + carrier.getName());
        }

        String trackingNumber = dto.getTrackingNumber();
        if (trackingNumber == null || trackingNumber.trim().isEmpty()) {
            trackingNumber = generateTrackingNumber(carrier.getCode());
        }

        if (deliveryRepository.existsByTrackingNumber(trackingNumber)) {
            throw new InvalidOperationException("Tracking number already exists: " + trackingNumber);
        }

        Delivery delivery = Delivery.builder()
                .order(order)
                .carrier(carrier)
                .trackingNumber(trackingNumber)
                .status(DeliveryStatus.PENDING)
                .estimatedDeliveryDate(dto.getEstimatedDeliveryDate())
                .notes(dto.getNotes())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        delivery = deliveryRepository.save(delivery);
        log.info("Delivery created successfully with id: {} and tracking number: {}",
                delivery.getId(), delivery.getTrackingNumber());

        return mapToResponseDTO(delivery);
    }

    /**
     * Récupérer toutes les livraisons
     */
    public List<DeliveryResponseDTO> getAllDeliveries() {
        log.info("Fetching all deliveries");
        return deliveryRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupérer une livraison par ID
     */
    public DeliveryResponseDTO getDeliveryById(Long deliveryId) {
        log.info("Fetching delivery with id: {}", deliveryId);

        Delivery delivery = deliveryRepository.findByIdWithDetails(deliveryId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery not found with id: " + deliveryId));

        return mapToResponseDTO(delivery);
    }

    /**
     * Récupérer une livraison par numéro de suivi
     */
    public DeliveryResponseDTO getDeliveryByTrackingNumber(String trackingNumber) {
        log.info("Fetching delivery with tracking number: {}", trackingNumber);

        Delivery delivery = deliveryRepository.findByTrackingNumber(trackingNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery not found with tracking number: " + trackingNumber));

        return mapToResponseDTO(delivery);
    }

    /**
     * Récupérer la livraison d'une commande
     */
    public DeliveryResponseDTO getDeliveryByOrderId(Long orderId) {
        log.info("Fetching delivery for order: {}", orderId);

        Delivery delivery = deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("No delivery found for order: " + orderId));

        return mapToResponseDTO(delivery);
    }

    /**
     * Récupérer les livraisons d'un acheteur
     */
    public List<DeliveryResponseDTO> getDeliveriesByBuyerId(Long buyerId) {
        log.info("Fetching deliveries for buyer: {}", buyerId);

        return deliveryRepository.findByBuyerId(buyerId).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupérer les livraisons d'un vendeur
     */
    public List<DeliveryResponseDTO> getDeliveriesBySellerId(Long sellerId) {
        log.info("Fetching deliveries for seller: {}", sellerId);

        return deliveryRepository.findBySellerId(sellerId).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupérer les livraisons par statut
     */
    public List<DeliveryResponseDTO> getDeliveriesByStatus(DeliveryStatus status) {
        log.info("Fetching deliveries with status: {}", status);

        return deliveryRepository.findByStatus(status).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupérer les livraisons d'un transporteur
     */
    public List<DeliveryResponseDTO> getDeliveriesByCarrierId(Long carrierId) {
        log.info("Fetching deliveries for carrier: {}", carrierId);

        return deliveryRepository.findByCarrierId(carrierId).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupérer les livraisons en retard
     */
    public List<DeliveryResponseDTO> getDelayedDeliveries() {
        log.info("Fetching delayed deliveries");

        List<DeliveryStatus> activeStatuses = List.of(
            DeliveryStatus.PENDING,
            DeliveryStatus.LABEL_GENERATED,
            DeliveryStatus.PICKED_UP,
            DeliveryStatus.IN_TRANSIT,
            DeliveryStatus.OUT_FOR_DELIVERY
        );

        return deliveryRepository.findDelayedDeliveries(DeliveryStatus.IN_TRANSIT, LocalDateTime.now()).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Mettre à jour une livraison
     */
    public DeliveryResponseDTO updateDelivery(Long deliveryId, DeliveryUpdateDTO dto) {
        log.info("Updating delivery: {}", deliveryId);

        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery not found with id: " + deliveryId));

        if (dto.getTrackingNumber() != null) {
            if (!dto.getTrackingNumber().equals(delivery.getTrackingNumber()) &&
                deliveryRepository.existsByTrackingNumber(dto.getTrackingNumber())) {
                throw new InvalidOperationException("Tracking number already exists: " + dto.getTrackingNumber());
            }
            delivery.setTrackingNumber(dto.getTrackingNumber());
        }

        if (dto.getStatus() != null) {
            updateDeliveryStatus(delivery, dto.getStatus());
        }

        if (dto.getLabelUrl() != null) {
            delivery.setLabelUrl(dto.getLabelUrl());
        }

        if (dto.getEstimatedDeliveryDate() != null) {
            delivery.setEstimatedDeliveryDate(dto.getEstimatedDeliveryDate());
        }

        if (dto.getNotes() != null) {
            delivery.setNotes(dto.getNotes());
        }

        delivery = deliveryRepository.save(delivery);
        log.info("Delivery updated successfully");

        return mapToResponseDTO(delivery);
    }

    /**
     * Mettre à jour le statut d'une livraison
     */
    public DeliveryResponseDTO updateDeliveryStatus(Long deliveryId, DeliveryStatus newStatus) {
        log.info("Updating delivery {} status to: {}", deliveryId, newStatus);

        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery not found with id: " + deliveryId));

        updateDeliveryStatus(delivery, newStatus);
        delivery = deliveryRepository.save(delivery);

        log.info("Delivery status updated successfully");
        return mapToResponseDTO(delivery);
    }

    /**
     * Générer l'étiquette de livraison
     */
    public DeliveryResponseDTO generateLabel(Long deliveryId) {
        log.info("Generating label for delivery: {}", deliveryId);

        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery not found with id: " + deliveryId));

        if (delivery.getStatus() != DeliveryStatus.PENDING) {
            throw new InvalidOperationException("Label can only be generated for pending deliveries");
        }

        // TODO: Intégration avec API du transporteur pour générer l'étiquette
        String labelUrl = "/labels/" + delivery.getTrackingNumber() + ".pdf";
        delivery.setLabelUrl(labelUrl);
        delivery.setStatus(DeliveryStatus.LABEL_GENERATED);

        delivery = deliveryRepository.save(delivery);
        log.info("Label generated successfully: {}", labelUrl);

        return mapToResponseDTO(delivery);
    }

    /**
     * Marquer comme expédiée
     */
    public DeliveryResponseDTO markAsShipped(Long deliveryId) {
        log.info("Marking delivery as shipped: {}", deliveryId);

        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery not found with id: " + deliveryId));

        if (delivery.getStatus() == DeliveryStatus.DELIVERED) {
            throw new InvalidOperationException("Delivery already delivered");
        }

        delivery.setStatus(DeliveryStatus.PICKED_UP);
        delivery.setShippedAt(LocalDateTime.now());

        delivery = deliveryRepository.save(delivery);
        log.info("Delivery marked as shipped");

        return mapToResponseDTO(delivery);
    }

    /**
     * Marquer comme livrée
     */
    public DeliveryResponseDTO markAsDelivered(Long deliveryId) {
        log.info("Marking delivery as delivered: {}", deliveryId);

        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery not found with id: " + deliveryId));

        delivery.setStatus(DeliveryStatus.DELIVERED);
        delivery.setDeliveredAt(LocalDateTime.now());

        delivery = deliveryRepository.save(delivery);
        log.info("Delivery marked as delivered");

        return mapToResponseDTO(delivery);
    }

    /**
     * Supprimer une livraison
     */
    public void deleteDelivery(Long deliveryId) {
        log.info("Deleting delivery: {}", deliveryId);

        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery not found with id: " + deliveryId));

        if (delivery.getStatus() != DeliveryStatus.PENDING) {
            throw new InvalidOperationException("Only pending deliveries can be deleted");
        }

        deliveryRepository.delete(delivery);
        log.info("Delivery deleted successfully");
    }

    private void updateDeliveryStatus(Delivery delivery, DeliveryStatus newStatus) {
        DeliveryStatus oldStatus = delivery.getStatus();
        delivery.setStatus(newStatus);

        switch (newStatus) {
            case PICKED_UP:
                if (delivery.getShippedAt() == null) {
                    delivery.setShippedAt(LocalDateTime.now());
                }
                break;
            case DELIVERED:
                if (delivery.getDeliveredAt() == null) {
                    delivery.setDeliveredAt(LocalDateTime.now());
                }
                break;
        }

        log.info("Delivery status changed from {} to {}", oldStatus, newStatus);
    }

    private String generateTrackingNumber(String carrierCode) {
        return carrierCode + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private DeliveryResponseDTO mapToResponseDTO(Delivery delivery) {
        Order order = delivery.getOrder();
        Carrier carrier = delivery.getCarrier();

        String trackingUrl = null;
        if (carrier.getTrackingUrlPattern() != null && delivery.getTrackingNumber() != null) {
            trackingUrl = carrier.getTrackingUrlPattern().replace("{trackingNumber}", delivery.getTrackingNumber());
        }

        Person buyer = order.getBuyer();
        String buyerName = buyer.getFirstName() + " " + buyer.getLastName();

        String deliveryAddress = ""; // TODO: Récupérer depuis order.deliveryAddress

        String productTitle = "N/A";
        if (order.getAuction() != null && order.getAuction().getProduct() != null) {
            productTitle = order.getAuction().getProduct().getTitle();
        } else if (order.getQuickSale() != null && order.getQuickSale().getProduct() != null) {
            productTitle = order.getQuickSale().getProduct().getTitle();
        }

        Person seller = order.getSeller();
        String sellerName = seller != null ? seller.getFirstName() + " " + seller.getLastName() : "N/A";

        CarrierDTO carrierDTO = CarrierDTO.builder()
                .id(carrier.getId())
                .name(carrier.getName())
                .code(carrier.getCode())
                .logo(carrier.getLogo())
                .trackingUrlPattern(carrier.getTrackingUrlPattern())
                .basePrice(carrier.getBasePrice())
                .active(carrier.getActive())
                .build();

        return DeliveryResponseDTO.builder()
                .id(delivery.getId())
                .orderId(order.getId())
                .orderReference(order.getOrderNumber())
                .carrier(carrierDTO)
                .trackingNumber(delivery.getTrackingNumber())
                .trackingUrl(trackingUrl)
                .status(delivery.getStatus())
                .labelUrl(delivery.getLabelUrl())
                .estimatedDeliveryDate(delivery.getEstimatedDeliveryDate())
                .shippedAt(delivery.getShippedAt())
                .deliveredAt(delivery.getDeliveredAt())
                .notes(delivery.getNotes())
                .createdAt(delivery.getCreatedAt())
                .updatedAt(delivery.getUpdatedAt())
                .buyerName(buyerName)
                .deliveryAddress(deliveryAddress)
                .productTitle(productTitle)
                .sellerName(sellerName)
                .build();
    }
}

