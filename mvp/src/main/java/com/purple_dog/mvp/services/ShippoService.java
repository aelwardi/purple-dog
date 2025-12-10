package com.purple_dog.mvp.services;

import com.purple_dog.mvp.config.ShippoConfig;
import com.purple_dog.mvp.dao.DeliveryRepository;
import com.purple_dog.mvp.dao.OrderRepository;
import com.purple_dog.mvp.dto.*;
import com.purple_dog.mvp.entities.*;
import com.purple_dog.mvp.exceptions.ResourceNotFoundException;
import com.shippo.exception.ShippoException;
import com.shippo.model.Parcel;
import com.shippo.model.Rate;
import com.shippo.model.Shipment;
import com.shippo.model.Transaction;
import com.shippo.model.Track;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShippoService {

    private final DeliveryRepository deliveryRepository;
    private final OrderRepository orderRepository;
    private final ShippoConfig shippoConfig;

    /**
     * Create a shipment and get shipping rates
     */
    @Transactional
    public ShippingRatesDTO createShipmentAndGetRates(CreateShipmentDTO request) {
        try {
            log.info("Creating shipment for order: {}", request.getOrderId());

            Order order = orderRepository.findById(request.getOrderId())
                    .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

            Map<String, Object> fromAddressMap = new HashMap<>();
            fromAddressMap.put("name", request.getFromName());
            fromAddressMap.put("street1", request.getFromStreet());
            fromAddressMap.put("city", request.getFromCity());
            fromAddressMap.put("zip", request.getFromZip());
            fromAddressMap.put("country", request.getFromCountry());
            if (request.getFromState() != null) fromAddressMap.put("state", request.getFromState());
            if (request.getFromPhone() != null) fromAddressMap.put("phone", request.getFromPhone());
            if (request.getFromEmail() != null) fromAddressMap.put("email", request.getFromEmail());

            com.shippo.model.Address fromAddress = com.shippo.model.Address.create(fromAddressMap);

            com.purple_dog.mvp.entities.Address shippingAddress = order.getShippingAddress();
            Map<String, Object> toAddressMap = new HashMap<>();
            toAddressMap.put("name", order.getBuyer().getFirstName() + " " + order.getBuyer().getLastName());
            toAddressMap.put("street1", shippingAddress.getStreet());
            toAddressMap.put("city", shippingAddress.getCity());
            toAddressMap.put("zip", shippingAddress.getPostalCode());
            toAddressMap.put("country", shippingAddress.getCountry());
            if (order.getBuyer().getPhone() != null) toAddressMap.put("phone", order.getBuyer().getPhone());
            if (order.getBuyer().getEmail() != null) toAddressMap.put("email", order.getBuyer().getEmail());

            com.shippo.model.Address toAddress = com.shippo.model.Address.create(toAddressMap);

            Map<String, Object> parcelMap = new HashMap<>();
            parcelMap.put("length", request.getLength().toString());
            parcelMap.put("width", request.getWidth().toString());
            parcelMap.put("height", request.getHeight().toString());
            parcelMap.put("distance_unit", "cm");
            parcelMap.put("weight", request.getWeight().toString());
            parcelMap.put("mass_unit", "kg");

            Parcel parcel = Parcel.create(parcelMap);

            Map<String, Object> shipmentMap = new HashMap<>();
            shipmentMap.put("address_from", fromAddress.getObjectId());
            shipmentMap.put("address_to", toAddress.getObjectId());
            shipmentMap.put("parcels", Collections.singletonList(parcel.getObjectId()));
            shipmentMap.put("async", false);

            Map<String, String> metadata = new HashMap<>();
            metadata.put("order_id", order.getId().toString());
            metadata.put("order_number", order.getOrderNumber());
            shipmentMap.put("metadata", metadata);

            Shipment shipment = Shipment.create(shipmentMap);

            log.info("Shipment created: {}", shipment.getObjectId());

            Delivery delivery = deliveryRepository.findByOrderId(order.getId())
                    .orElse(Delivery.builder()
                            .order(order)
                            .status(DeliveryStatus.PENDING)
                            .build());

            delivery.setShippoShipmentId(shipment.getObjectId());
            delivery.setWeight(request.getWeight());
            delivery.setLength(request.getLength());
            delivery.setWidth(request.getWidth());
            delivery.setHeight(request.getHeight());
            delivery.setFromAddress(fromAddressMapToJson(fromAddressMap));
            delivery.setToAddress(toAddressMapToJson(toAddressMap));

            deliveryRepository.save(delivery);

            List<ShippingRatesDTO.ShippingRate> ratesList = shipment.getRates().stream()
                    .map(this::mapShippoRateToDTO)
                    .collect(Collectors.toList());

            return ShippingRatesDTO.builder()
                    .shipmentId(shipment.getObjectId())
                    .rates(ratesList)
                    .build();

        } catch (ShippoException e) {
            log.error("Error creating shipment: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create shipment: " + e.getMessage());
        }
    }

    /**
     * Purchase a shipping label
     */
    @Transactional
    public DeliveryResponseDTO purchaseLabel(Long deliveryId, PurchaseLabelDTO request) {
        try {
            log.info("Purchasing label for delivery: {}", deliveryId);

            Delivery delivery = deliveryRepository.findById(deliveryId)
                    .orElseThrow(() -> new ResourceNotFoundException("Delivery not found"));

            Map<String, Object> transactionMap = new HashMap<>();
            transactionMap.put("rate", request.getRateId());
            transactionMap.put("label_file_type", request.getLabelFileType() != null ? request.getLabelFileType() : "PDF");
            transactionMap.put("async", request.getAsync() != null ? request.getAsync() : false);

            Transaction transaction = Transaction.create(transactionMap);

            log.info("Label purchased: {}", transaction.getObjectId());

            delivery.setShippoTransactionId(transaction.getObjectId());
            delivery.setShippoTrackingNumber(String.valueOf(transaction.getTrackingNumber()));
            delivery.setTrackingNumber(String.valueOf(transaction.getTrackingNumber()));
            delivery.setTrackingUrlProvider(String.valueOf(transaction.getTrackingUrlProvider()));
            delivery.setLabelUrl(String.valueOf(transaction.getLabelUrl()));
            delivery.setLabelFileType(request.getLabelFileType());

            Object rateObj = transaction.getRate();
            if (rateObj instanceof Rate rate) {
                delivery.setCarrierName(String.valueOf(rate.getProvider()));

                Object serviceLevelObj = rate.getServicelevel();
                if (serviceLevelObj instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> serviceLevel = (Map<String, Object>) serviceLevelObj;
                    delivery.setServiceLevelName(String.valueOf(serviceLevel.get("name")));
                    delivery.setServiceLevelToken(String.valueOf(serviceLevel.get("token")));
                }

                Object amountObj = rate.getAmount();
                if (amountObj != null) {
                    delivery.setShippingCost(new BigDecimal(String.valueOf(amountObj)));
                    delivery.setCurrency(String.valueOf(rate.getCurrency()));
                }
            }

            delivery.setStatus(DeliveryStatus.LABEL_CREATED);
            delivery.setShippedAt(LocalDateTime.now());

            delivery = deliveryRepository.save(delivery);

            log.info("Delivery updated with label info");

            return mapToDeliveryResponseDTO(delivery);

        } catch (ShippoException e) {
            log.error("Error purchasing label: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to purchase label: " + e.getMessage());
        }
    }

    /**
     * Get tracking information
     */
    public DeliveryResponseDTO getTrackingInfo(Long deliveryId) {
        try {
            log.info("Getting tracking info for delivery: {}", deliveryId);

            Delivery delivery = deliveryRepository.findById(deliveryId)
                    .orElseThrow(() -> new ResourceNotFoundException("Delivery not found"));

            if (delivery.getShippoTrackingNumber() == null) {
                throw new RuntimeException("No tracking number available");
            }

            Track tracking = Track.getTrackingInfo(
                    delivery.getCarrierName(),
                    delivery.getShippoTrackingNumber(),
                    null // apiKey (will use default from Shippo.setApiKey)
            );

            updateDeliveryFromTracking(delivery, tracking);
            delivery = deliveryRepository.save(delivery);

            return mapToDeliveryResponseDTO(delivery);

        } catch (ShippoException e) {
            log.error("Error getting tracking info: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to get tracking info: " + e.getMessage());
        }
    }

    /**
     * Get delivery by ID
     */
    public DeliveryResponseDTO getDelivery(Long deliveryId) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery not found"));

        return mapToDeliveryResponseDTO(delivery);
    }

    /**
     * Get deliveries for an order
     */
    public List<DeliveryResponseDTO> getOrderDeliveries(Long orderId) {
        return deliveryRepository.findByOrderId(orderId).stream()
                .map(this::mapToDeliveryResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get all deliveries with pagination
     */
    public Page<DeliveryResponseDTO> getAllDeliveries(Pageable pageable) {
        return deliveryRepository.findAll(pageable)
                .map(this::mapToDeliveryResponseDTO);
    }

    /**
     * Validate an address
     */
    public Map<String, Object> validateAddress(Map<String, Object> addressMap) {
        try {
            log.info("Validating address");

            com.shippo.model.Address address = com.shippo.model.Address.create(addressMap);

            Map<String, Object> result = new HashMap<>();
            Object validationResults = address.getValidationResults();
            if (validationResults != null) {
                result.put("isValid", validationResults);
                result.put("validationResults", String.valueOf(validationResults));
            } else {
                result.put("isValid", true);
            }
            result.put("objectId", address.getObjectId());

            return result;

        } catch (ShippoException e) {
            log.error("Error validating address: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to validate address: " + e.getMessage());
        }
    }


    private ShippingRatesDTO.ShippingRate mapShippoRateToDTO(Rate rate) {
        Object amountObj = rate.getAmount();
        Object serviceLevelObj = rate.getServicelevel();
        Object messagesObj = rate.getMessages();

        String serviceLevelName = "";
        String serviceLevelToken = "";

        if (serviceLevelObj instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> serviceLevel = (Map<String, Object>) serviceLevelObj;
            serviceLevelName = String.valueOf(serviceLevel.get("name"));
            serviceLevelToken = String.valueOf(serviceLevel.get("token"));
        }

        boolean available = messagesObj == null || (messagesObj instanceof List && ((List<?>) messagesObj).isEmpty());

        return ShippingRatesDTO.ShippingRate.builder()
                .rateId(rate.getObjectId())
                .provider(String.valueOf(rate.getProvider()))
                .serviceLevelName(serviceLevelName)
                .serviceLevelToken(serviceLevelToken)
                .amount(amountObj != null ? new BigDecimal(String.valueOf(amountObj)) : null)
                .currency(String.valueOf(rate.getCurrency()))
                .estimatedDays(null) // Not available in v3.2.0
                .durationTerms(rate.getAttributes() != null && rate.getAttributes() instanceof Map ?
                    String.valueOf(((Map<?, ?>) rate.getAttributes()).get("duration_terms")) : "")
                .available(available)
                .errorMessage(!available ? String.valueOf(messagesObj) : null)
                .build();
    }

    private void updateDeliveryFromTracking(Delivery delivery, Track tracking) {
        Object trackingStatusObj = tracking.getTrackingStatus();
        String statusString = String.valueOf(trackingStatusObj);
        delivery.setTrackingStatus(statusString);

        if (statusString != null) {
            switch (statusString.toUpperCase()) {
                case "DELIVERED":
                    delivery.setStatus(DeliveryStatus.DELIVERED);
                    delivery.setDeliveredAt(LocalDateTime.now());
                    break;
                case "TRANSIT":
                case "IN_TRANSIT":
                    delivery.setStatus(DeliveryStatus.IN_TRANSIT);
                    if (delivery.getInTransitAt() == null) {
                        delivery.setInTransitAt(LocalDateTime.now());
                    }
                    break;
                case "FAILURE":
                case "RETURNED":
                    delivery.setStatus(DeliveryStatus.FAILED);
                    break;
                case "UNKNOWN":
                default:
                    delivery.setStatus(DeliveryStatus.PENDING);
                    break;
            }
        }

        Object trackingHistory = tracking.getTrackingHistory();
        if (trackingHistory != null && trackingHistory instanceof List && !((List<?>) trackingHistory).isEmpty()) {
            log.debug("Tracking history available but ETA parsing not implemented");
        }
    }

    private DeliveryResponseDTO mapToDeliveryResponseDTO(Delivery delivery) {
        Order order = delivery.getOrder();

        return DeliveryResponseDTO.builder()
                .id(delivery.getId())
                .orderId(order.getId())
                .orderReference(order.getOrderNumber())
                .shippoShipmentId(delivery.getShippoShipmentId())
                .shippoTransactionId(delivery.getShippoTransactionId())
                .shippoTrackingNumber(delivery.getShippoTrackingNumber())
                .carrier(delivery.getCarrier() != null ? mapCarrierToDTO(delivery.getCarrier()) : null)
                .carrierName(delivery.getCarrierName())
                .serviceLevelName(delivery.getServiceLevelName())
                .trackingNumber(delivery.getTrackingNumber())
                .trackingStatus(delivery.getTrackingStatus())
                .trackingUrl(delivery.getTrackingUrlProvider())
                .status(delivery.getStatus())
                .labelUrl(delivery.getLabelUrl())
                .commercialInvoiceUrl(delivery.getCommercialInvoiceUrl())
                .estimatedDeliveryDate(delivery.getEstimatedDeliveryDate())
                .shippedAt(delivery.getShippedAt())
                .deliveredAt(delivery.getDeliveredAt())
                .inTransitAt(delivery.getInTransitAt())
                .shippingCost(delivery.getShippingCost())
                .currency(delivery.getCurrency())
                .notes(delivery.getNotes())
                .createdAt(delivery.getCreatedAt())
                .updatedAt(delivery.getUpdatedAt())
                .buyerName(order.getBuyer().getFirstName() + " " + order.getBuyer().getLastName())
                .deliveryAddress(formatAddress(order.getShippingAddress()))
                .sellerName(order.getSeller().getFirstName() + " " + order.getSeller().getLastName())
                .build();
    }

    private CarrierDTO mapCarrierToDTO(Carrier carrier) {
        return CarrierDTO.builder()
                .id(carrier.getId())
                .name(carrier.getName())
                .code(carrier.getCode())
                .active(carrier.getActive())
                .build();
    }

    private String formatAddress(com.purple_dog.mvp.entities.Address address) {
        return String.format("%s, %s, %s %s",
                address.getStreet(),
                address.getCity(),
                address.getPostalCode(),
                address.getCountry());
    }

    private String fromAddressMapToJson(Map<String, Object> addressMap) {
        try {
            return addressMap.toString();
        } catch (Exception e) {
            return "";
        }
    }

    private String toAddressMapToJson(Map<String, Object> addressMap) {
        try {
            return addressMap.toString();
        } catch (Exception e) {
            return "";
        }
    }
}

