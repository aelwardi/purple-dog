package com.purple_dog.mvp.services;

import com.purple_dog.mvp.dao.*;
import com.purple_dog.mvp.dto.OrderCreateDTO;
import com.purple_dog.mvp.dto.OrderResponseDTO;
import com.purple_dog.mvp.dto.OrderUpdateDTO;
import com.purple_dog.mvp.entities.*;
import com.purple_dog.mvp.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final PersonRepository personRepository;
    private final AddressRepository addressRepository;
    private final NotificationService notificationService;
    private final InAppNotificationService inAppNotificationService;
    private final QuickSaleRepository quickSaleRepository;
    private final AuctionRepository auctionRepository;
    private final ProductRepository productRepository;

    public OrderResponseDTO createOrder(OrderCreateDTO dto) {
        log.debug("Creating order for buyer: {} and seller: {}", dto.getBuyerId(), dto.getSellerId());

        Person buyer = personRepository.findById(dto.getBuyerId())
                .orElseThrow(() -> new ResourceNotFoundException("Buyer not found with id: " + dto.getBuyerId()));

        Person seller = personRepository.findById(dto.getSellerId())
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found with id: " + dto.getSellerId()));

        Address shippingAddress = addressRepository.findById(dto.getShippingAddressId())
                .orElseThrow(() -> new ResourceNotFoundException("Shipping address not found with id: " + dto.getShippingAddressId()));

        Address billingAddress = addressRepository.findById(dto.getBillingAddressId())
                .orElseThrow(() -> new ResourceNotFoundException("Billing address not found with id: " + dto.getBillingAddressId()));

        Order order = new Order();
        order.setOrderNumber(generateOrderNumber());
        order.setBuyer(buyer);
        order.setSeller(seller);
        // Associate quickSale or auction if provided
        if (dto.getQuickSaleId() != null) {
            Long providedQsId = dto.getQuickSaleId();
            QuickSale quickSale = quickSaleRepository.findByIdWithDetails(providedQsId).orElse(null);
            if (quickSale == null) {
                log.warn("QuickSale with id {} not found by id. Trying fallback: treat value as productId...", providedQsId);
                // Fallback: maybe the client sent a productId by mistake
                quickSale = quickSaleRepository.findByProductId(providedQsId).orElse(null);
                if (quickSale != null) {
                    log.info("Found QuickSale by productId fallback: quickSaleId={}, productId={}", quickSale.getId(), providedQsId);
                }
            }

            if (quickSale == null) {
                throw new ResourceNotFoundException("QuickSale not found with id or productId: " + providedQsId);
            }

            order.setQuickSale(quickSale);
            // Reserve the quick sale immediately for this order
            try {
                quickSale.setIsAvailable(false);
                quickSale.setSoldAt(null); // will be set upon payment
                quickSaleRepository.save(quickSale);
            } catch (Exception e) {
                log.warn("Failed to reserve quickSale {}: {}", quickSale != null ? quickSale.getId() : null, e.getMessage());
            }
            log.debug("Associating quickSale id={} to order {}", quickSale.getId(), order.getOrderNumber());
        } else if (dto.getProductId() != null) {
            // If quickSaleId not provided, try to find a quick sale by productId
            Long productId = dto.getProductId();
            QuickSale quickSale = quickSaleRepository.findByProductId(productId).orElse(null);
            if (quickSale != null) {
                order.setQuickSale(quickSale);
                try {
                    quickSale.setIsAvailable(false);
                    quickSaleRepository.save(quickSale);
                } catch (Exception e) {
                    log.warn("Failed to reserve quickSale {}: {}", quickSale != null ? quickSale.getId() : null, e.getMessage());
                }
                log.debug("Associated QuickSale (found by productId) id={} to order {}", quickSale.getId(), order.getOrderNumber());
            } else {
                log.debug("No QuickSale found for productId {} during order creation - attempting fallback: create or use product's quickSale", productId);
                // Fallback: try to find product and create a quickSale if product.saleType==QUICK_SALE
                try {
                    var product = productRepository.findById(productId).orElse(null);
                    if (product != null) {
                        if (product.getQuickSale() != null) {
                            order.setQuickSale(product.getQuickSale());
                            try {
                                product.getQuickSale().setIsAvailable(false);
                                quickSaleRepository.save(product.getQuickSale());
                            } catch (Exception e) {
                                log.warn("Failed to reserve quickSale from product relation {}: {}", product.getQuickSale() != null ? product.getQuickSale().getId() : null, e.getMessage());
                            }
                            log.debug("Associated QuickSale from product relation id={} to order {}", product.getQuickSale().getId(), order.getOrderNumber());
                        } else if (product.getSaleType() == SaleType.QUICK_SALE) {
                            QuickSale newQs = new QuickSale();
                            newQs.setProduct(product);
                            newQs.setFixedPrice(product.getEstimatedValue());
                            newQs.setIsAvailable(false); // immediately reserved for this order
                            newQs.setCreatedAt(LocalDateTime.now());
                            quickSaleRepository.save(newQs);
                            order.setQuickSale(newQs);
                            log.info("Created fallback QuickSale id={} for product {} and associated with order {}", newQs.getId(), productId, order.getOrderNumber());
                        } else {
                            log.debug("Product {} is not a QUICK_SALE, no quickSale created", productId);
                        }
                    } else {
                        log.warn("Product {} not found during quickSale fallback", productId);
                    }
                } catch (Exception e) {
                    log.warn("Exception during quickSale fallback for product {}: {}", productId, e.getMessage());
                }
            }
        }
        if (dto.getAuctionId() != null) {
            Auction auction = auctionRepository.findById(dto.getAuctionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Auction not found with id: " + dto.getAuctionId()));
            order.setAuction(auction);
            log.debug("Associating auction id={} to order {}", auction.getId(), order.getOrderNumber());
        }

        order.setProductPrice(dto.getProductPrice());
        order.setShippingCost(dto.getShippingCost() != null ? dto.getShippingCost() : BigDecimal.ZERO);
        order.setPlatformFee(dto.getPlatformFee() != null ? dto.getPlatformFee() : BigDecimal.ZERO);
        order.setShippingAddress(shippingAddress);
        order.setBillingAddress(billingAddress);
        
        // Calculer le montant total
        BigDecimal total = order.getProductPrice()
                .add(order.getShippingCost())
                .add(order.getPlatformFee());
        order.setTotalAmount(total);

        Order savedOrder = orderRepository.save(order);
        log.info("Order created successfully with ID: {}", savedOrder.getId());

        // Envoyer email de confirmation à l'acheteur et notification au vendeur
        notificationService.sendOrderConfirmationEmail(savedOrder, buyer, seller);

        // Créer une notification in-app pour le vendeur
        try {
            inAppNotificationService.createOrderNotification(
                seller.getId(),
                savedOrder.getOrderNumber(),
                savedOrder.getTotalAmount().doubleValue()
            );
            log.info("✅ In-app notification sent to seller {}", seller.getId());
        } catch (Exception e) {
            log.error("❌ Failed to create order notification for seller: {}", e.getMessage());
        }

        return mapToResponseDTO(savedOrder);
    }

    @Transactional(readOnly = true)
    public OrderResponseDTO getOrderById(Long id) {
        log.debug("Fetching order with ID: {}", id);
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        return mapToResponseDTO(order);
    }

    @Transactional(readOnly = true)
    public OrderResponseDTO getOrderByOrderNumber(String orderNumber) {
        log.debug("Fetching order with order number: {}", orderNumber);
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with order number: " + orderNumber));
        return mapToResponseDTO(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getAllOrders() {
        log.debug("Fetching all orders");
        return orderRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getOrdersByBuyer(Long buyerId) {
        log.debug("Fetching orders for buyer: {}", buyerId);
        return orderRepository.findByBuyerId(buyerId).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getOrdersBySeller(Long sellerId) {
        log.debug("Fetching orders for seller: {}", sellerId);
        return orderRepository.findBySellerId(sellerId).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getOrdersByStatus(OrderStatus status) {
        log.debug("Fetching orders with status: {}", status);
        return orderRepository.findByStatus(status).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getOrdersByPerson(Long personId) {
        log.debug("Fetching orders for person: {}", personId);
        return orderRepository.findByPersonId(personId).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public OrderResponseDTO updateOrder(Long id, OrderUpdateDTO dto) {
        log.debug("Updating order with ID: {}", id);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        if (dto.getStatus() != null) {
            order.setStatus(dto.getStatus());
            if (dto.getStatus() == OrderStatus.DELIVERED || dto.getStatus() == OrderStatus.CANCELLED) {
                order.setCompletedAt(LocalDateTime.now());
            }
        }

        if (dto.getShippingCost() != null) {
            order.setShippingCost(dto.getShippingCost());
            recalculateTotal(order);
        }

        if (dto.getPlatformFee() != null) {
            order.setPlatformFee(dto.getPlatformFee());
            recalculateTotal(order);
        }

        if (dto.getShippingAddressId() != null) {
            Address shippingAddress = addressRepository.findById(dto.getShippingAddressId())
                    .orElseThrow(() -> new ResourceNotFoundException("Shipping address not found"));
            order.setShippingAddress(shippingAddress);
        }

        if (dto.getBillingAddressId() != null) {
            Address billingAddress = addressRepository.findById(dto.getBillingAddressId())
                    .orElseThrow(() -> new ResourceNotFoundException("Billing address not found"));
            order.setBillingAddress(billingAddress);
        }

        Order updatedOrder = orderRepository.save(order);
        log.info("Order updated successfully with ID: {}", updatedOrder.getId());

        return mapToResponseDTO(updatedOrder);
    }

    public OrderResponseDTO updateOrderStatus(Long id, OrderStatus status) {
        log.debug("Updating order status to {} for order ID: {}", status, id);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        order.setStatus(status);
        if (status == OrderStatus.DELIVERED || status == OrderStatus.CANCELLED) {
            order.setCompletedAt(LocalDateTime.now());
        }

        Order updatedOrder = orderRepository.save(order);
        log.info("Order status updated successfully for ID: {}", updatedOrder.getId());

        return mapToResponseDTO(updatedOrder);
    }

    public void deleteOrder(Long id) {
        log.debug("Deleting order with ID: {}", id);

        if (!orderRepository.existsById(id)) {
            throw new ResourceNotFoundException("Order not found with id: " + id);
        }

        orderRepository.deleteById(id);
        log.info("Order deleted successfully with ID: {}", id);
    }

    @Transactional(readOnly = true)
    public long countOrdersByStatus(OrderStatus status) {
        return orderRepository.countByStatus(status);
    }

    @Transactional(readOnly = true)
    public long countOrdersByBuyer(Long buyerId) {
        return orderRepository.countByBuyerId(buyerId);
    }

    @Transactional(readOnly = true)
    public long countOrdersBySeller(Long sellerId) {
        return orderRepository.countBySellerId(sellerId);
    }

    private void recalculateTotal(Order order) {
        BigDecimal total = order.getProductPrice()
                .add(order.getShippingCost())
                .add(order.getPlatformFee());
        order.setTotalAmount(total);
    }

    private String generateOrderNumber() {
        String orderNumber;
        do {
            orderNumber = "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (orderRepository.existsByOrderNumber(orderNumber));
        return orderNumber;
    }

    private OrderResponseDTO mapToResponseDTO(Order order) {
        return OrderResponseDTO.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .buyerId(order.getBuyer().getId())
                .buyerName(order.getBuyer().getFirstName() + " " + order.getBuyer().getLastName())
                .sellerId(order.getSeller().getId())
                .sellerName(order.getSeller().getFirstName() + " " + order.getSeller().getLastName())
                .auctionId(order.getAuction() != null ? order.getAuction().getId() : null)
                .quickSaleId(order.getQuickSale() != null ? order.getQuickSale().getId() : null)
                .productPrice(order.getProductPrice())
                .shippingCost(order.getShippingCost())
                .platformFee(order.getPlatformFee())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .shippingAddressId(order.getShippingAddress().getId())
                .billingAddressId(order.getBillingAddress().getId())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .completedAt(order.getCompletedAt())
                .build();
    }
}
