package com.purple_dog.mvp.services;

import com.purple_dog.mvp.dao.OrderRepository;
import com.purple_dog.mvp.dto.OrderCreateDTO;
import com.purple_dog.mvp.dto.OrderResponseDTO;
import com.purple_dog.mvp.dto.OrderUpdateDTO;
import com.purple_dog.mvp.entities.*;
import com.purple_dog.mvp.dao.PersonRepository;
import com.purple_dog.mvp.dao.AddressRepository;
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
