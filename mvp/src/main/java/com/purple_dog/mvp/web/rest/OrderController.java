package com.purple_dog.mvp.web.rest;

import com.purple_dog.mvp.dto.OrderCreateDTO;
import com.purple_dog.mvp.dto.OrderResponseDTO;
import com.purple_dog.mvp.dto.OrderUpdateDTO;
import com.purple_dog.mvp.entities.OrderStatus;
import com.purple_dog.mvp.services.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder(@Valid @RequestBody OrderCreateDTO dto) {
        log.info("POST /orders - Creating order for buyer: {} and seller: {}", dto.getBuyerId(), dto.getSellerId());
        OrderResponseDTO created = orderService.createOrder(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> getOrderById(@PathVariable Long id) {
        log.info("GET /orders/{} - Fetching order", id);
        OrderResponseDTO order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/number/{orderNumber}")
    public ResponseEntity<OrderResponseDTO> getOrderByOrderNumber(@PathVariable String orderNumber) {
        log.info("GET /orders/number/{} - Fetching order by order number", orderNumber);
        OrderResponseDTO order = orderService.getOrderByOrderNumber(orderNumber);
        return ResponseEntity.ok(order);
    }

    @GetMapping
    public ResponseEntity<List<OrderResponseDTO>> getAllOrders() {
        log.info("GET /orders - Fetching all orders");
        List<OrderResponseDTO> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/buyer/{buyerId}")
    public ResponseEntity<List<OrderResponseDTO>> getOrdersByBuyer(@PathVariable Long buyerId) {
        log.info("GET /orders/buyer/{} - Fetching orders by buyer", buyerId);
        List<OrderResponseDTO> orders = orderService.getOrdersByBuyer(buyerId);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<List<OrderResponseDTO>> getOrdersBySeller(@PathVariable Long sellerId) {
        log.info("GET /orders/seller/{} - Fetching orders by seller", sellerId);
        List<OrderResponseDTO> orders = orderService.getOrdersBySeller(sellerId);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<OrderResponseDTO>> getOrdersByStatus(@PathVariable OrderStatus status) {
        log.info("GET /orders/status/{} - Fetching orders by status", status);
        List<OrderResponseDTO> orders = orderService.getOrdersByStatus(status);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/person/{personId}")
    public ResponseEntity<List<OrderResponseDTO>> getOrdersByPerson(@PathVariable Long personId) {
        log.info("GET /orders/person/{} - Fetching orders by person (buyer or seller)", personId);
        List<OrderResponseDTO> orders = orderService.getOrdersByPerson(personId);
        return ResponseEntity.ok(orders);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> updateOrder(
            @PathVariable Long id,
            @Valid @RequestBody OrderUpdateDTO dto) {
        log.info("PUT /orders/{} - Updating order", id);
        OrderResponseDTO updated = orderService.updateOrder(id, dto);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponseDTO> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam OrderStatus status) {
        log.info("PATCH /orders/{}/status - Updating order status to {}", id, status);
        OrderResponseDTO updated = orderService.updateOrderStatus(id, status);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteOrder(@PathVariable Long id) {
        log.info("DELETE /orders/{} - Deleting order", id);
        orderService.deleteOrder(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Order deleted successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/count/status/{status}")
    public ResponseEntity<Map<String, Long>> countOrdersByStatus(@PathVariable OrderStatus status) {
        log.info("GET /orders/count/status/{} - Counting orders by status", status);
        Map<String, Long> count = new HashMap<>();
        count.put("count", orderService.countOrdersByStatus(status));
        return ResponseEntity.ok(count);
    }

    @GetMapping("/count/buyer/{buyerId}")
    public ResponseEntity<Map<String, Long>> countOrdersByBuyer(@PathVariable Long buyerId) {
        log.info("GET /orders/count/buyer/{} - Counting orders by buyer", buyerId);
        Map<String, Long> count = new HashMap<>();
        count.put("count", orderService.countOrdersByBuyer(buyerId));
        return ResponseEntity.ok(count);
    }

    @GetMapping("/count/seller/{sellerId}")
    public ResponseEntity<Map<String, Long>> countOrdersBySeller(@PathVariable Long sellerId) {
        log.info("GET /orders/count/seller/{} - Counting orders by seller", sellerId);
        Map<String, Long> count = new HashMap<>();
        count.put("count", orderService.countOrdersBySeller(sellerId));
        return ResponseEntity.ok(count);
    }
}
