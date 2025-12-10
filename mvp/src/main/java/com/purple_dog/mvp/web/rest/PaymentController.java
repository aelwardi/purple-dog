package com.purple_dog.mvp.web.rest;

import com.purple_dog.mvp.dto.PaymentCreateDTO;
import com.purple_dog.mvp.dto.PaymentResponseDTO;
import com.purple_dog.mvp.dto.RefundRequestDTO;
import com.purple_dog.mvp.entities.PaymentStatus;
import com.purple_dog.mvp.services.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * Créer un paiement
     */
    @PostMapping
    public ResponseEntity<PaymentResponseDTO> createPayment(@Valid @RequestBody PaymentCreateDTO dto) {
        log.info("Request to create payment for order: {}", dto.getOrderId());
        PaymentResponseDTO response = paymentService.createPayment(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Traiter un paiement
     */
    @PostMapping("/{paymentId}/process")
    public ResponseEntity<PaymentResponseDTO> processPayment(
            @PathVariable Long paymentId,
            @RequestParam(required = false) String stripeToken) {

        log.info("Request to process payment: {}", paymentId);
        PaymentResponseDTO response = paymentService.processPayment(paymentId, stripeToken);
        return ResponseEntity.ok(response);
    }

    /**
     * Récupérer un paiement par ID
     */
    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentResponseDTO> getPaymentById(@PathVariable Long paymentId) {
        log.info("Request to get payment: {}", paymentId);
        PaymentResponseDTO payment = paymentService.getPaymentById(paymentId);
        return ResponseEntity.ok(payment);
    }

    /**
     * Récupérer le paiement d'une commande
     */
    @GetMapping("/order/{orderId}")
    public ResponseEntity<PaymentResponseDTO> getPaymentByOrderId(@PathVariable Long orderId) {
        log.info("Request to get payment for order: {}", orderId);
        PaymentResponseDTO payment = paymentService.getPaymentByOrderId(orderId);
        return ResponseEntity.ok(payment);
    }

    /**
     * Récupérer les paiements d'un utilisateur
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PaymentResponseDTO>> getUserPayments(@PathVariable Long userId) {
        log.info("Request to get payments for user: {}", userId);
        List<PaymentResponseDTO> payments = paymentService.getUserPayments(userId);
        return ResponseEntity.ok(payments);
    }

    /**
     * Récupérer les paiements par statut
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<PaymentResponseDTO>> getPaymentsByStatus(@PathVariable PaymentStatus status) {
        log.info("Request to get payments with status: {}", status);
        List<PaymentResponseDTO> payments = paymentService.getPaymentsByStatus(status);
        return ResponseEntity.ok(payments);
    }

    /**
     * Récupérer les paiements d'un vendeur
     */
    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<List<PaymentResponseDTO>> getSellerPayments(@PathVariable Long sellerId) {
        log.info("Request to get payments for seller: {}", sellerId);
        List<PaymentResponseDTO> payments = paymentService.getSellerPayments(sellerId);
        return ResponseEntity.ok(payments);
    }

    /**
     * Calculer les gains d'un vendeur
     */
    @GetMapping("/seller/{sellerId}/earnings")
    public ResponseEntity<BigDecimal> calculateSellerEarnings(@PathVariable Long sellerId) {
        log.info("Request to calculate earnings for seller: {}", sellerId);
        BigDecimal earnings = paymentService.calculateSellerEarnings(sellerId);
        return ResponseEntity.ok(earnings);
    }

    /**
     * Rembourser un paiement
     */
    @PostMapping("/{paymentId}/refund")
    public ResponseEntity<PaymentResponseDTO> refundPayment(
            @PathVariable Long paymentId,
            @Valid @RequestBody RefundRequestDTO dto) {

        log.info("Request to refund payment: {}", paymentId);
        PaymentResponseDTO response = paymentService.refundPayment(paymentId, dto);
        return ResponseEntity.ok(response);
    }

    /**
     * Annuler un paiement
     */
    @PostMapping("/{paymentId}/cancel")
    public ResponseEntity<PaymentResponseDTO> cancelPayment(@PathVariable Long paymentId) {
        log.info("Request to cancel payment: {}", paymentId);
        PaymentResponseDTO response = paymentService.cancelPayment(paymentId);
        return ResponseEntity.ok(response);
    }

    /**
     * Compter les paiements par statut
     */
    @GetMapping("/status/{status}/count")
    public ResponseEntity<Long> countPaymentsByStatus(@PathVariable PaymentStatus status) {
        log.info("Request to count payments with status: {}", status);
        long count = paymentService.countPaymentsByStatus(status);
        return ResponseEntity.ok(count);
    }
}

