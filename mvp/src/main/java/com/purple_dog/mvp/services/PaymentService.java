package com.purple_dog.mvp.services;

import com.purple_dog.mvp.dao.OrderRepository;
import com.purple_dog.mvp.dao.PaymentRepository;
import com.purple_dog.mvp.dto.PaymentCreateDTO;
import com.purple_dog.mvp.dto.PaymentResponseDTO;
import com.purple_dog.mvp.dto.RefundRequestDTO;
import com.purple_dog.mvp.entities.*;
import com.purple_dog.mvp.exceptions.InvalidOperationException;
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
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    /**
     * Créer un paiement
     */
    public PaymentResponseDTO createPayment(PaymentCreateDTO dto) {
        log.info("Creating payment for order: {}", dto.getOrderId());

        Order order = orderRepository.findById(dto.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + dto.getOrderId()));

        if (paymentRepository.existsByOrderId(dto.getOrderId())) {
            throw new InvalidOperationException("Payment already exists for this order");
        }

        if (dto.getAmount().compareTo(order.getTotalAmount()) != 0) {
            throw new InvalidOperationException("Payment amount does not match order total");
        }

        String paymentIntentId = generatePaymentIntentId();

        Payment payment = Payment.builder()
                .order(order)
                .paymentIntentId(paymentIntentId)
                .amount(dto.getAmount())
                .currency(dto.getCurrency() != null ? dto.getCurrency() : "EUR")
                .paymentMethod(dto.getPaymentMethod())
                .status(PaymentStatus.PENDING)
                .stripeCustomerId(dto.getStripeCustomerId())
                .createdAt(LocalDateTime.now())
                .build();

        payment = paymentRepository.save(payment);
        log.info("Payment created with id: {} and intent: {}", payment.getId(), paymentIntentId);

        return mapToResponseDTO(payment);
    }

    /**
     * Traiter un paiement (simulation ou intégration Stripe)
     */
    public PaymentResponseDTO processPayment(Long paymentId, String stripeToken) {
        log.info("Processing payment: {}", paymentId);

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + paymentId));

        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new InvalidOperationException("Payment already processed");
        }

        payment.setStatus(PaymentStatus.PROCESSING);
        payment = paymentRepository.save(payment);

        try {
            // TODO: Intégration avec Stripe API
            // Pour l'instant, simulation de succès
            String chargeId = "ch_" + UUID.randomUUID().toString().substring(0, 24);

            payment.setStripeChargeId(chargeId);
            payment.setStatus(PaymentStatus.SUCCEEDED);
            payment.setProcessedAt(LocalDateTime.now());

            Order order = payment.getOrder();
            order.setStatus(OrderStatus.PAID);
            orderRepository.save(order);

            log.info("Payment processed successfully: {}", chargeId);

        } catch (Exception e) {
            log.error("Payment processing failed: {}", e.getMessage());
            payment.setStatus(PaymentStatus.FAILED);
            payment.setErrorMessage(e.getMessage());
        }

        payment = paymentRepository.save(payment);
        return mapToResponseDTO(payment);
    }

    /**
     * Récupérer un paiement par ID
     */
    public PaymentResponseDTO getPaymentById(Long paymentId) {
        log.info("Fetching payment: {}", paymentId);

        Payment payment = paymentRepository.findByIdWithDetails(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + paymentId));

        return mapToResponseDTO(payment);
    }

    /**
     * Récupérer le paiement d'une commande
     */
    public PaymentResponseDTO getPaymentByOrderId(Long orderId) {
        log.info("Fetching payment for order: {}", orderId);

        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("No payment found for order: " + orderId));

        return mapToResponseDTO(payment);
    }

    /**
     * Récupérer les paiements d'un utilisateur
     */
    public List<PaymentResponseDTO> getUserPayments(Long userId) {
        log.info("Fetching payments for user: {}", userId);

        return paymentRepository.findByUserId(userId).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupérer les paiements par statut
     */
    public List<PaymentResponseDTO> getPaymentsByStatus(PaymentStatus status) {
        log.info("Fetching payments with status: {}", status);

        return paymentRepository.findByStatus(status).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupérer les paiements réussis d'un vendeur
     */
    public List<PaymentResponseDTO> getSellerPayments(Long sellerId) {
        log.info("Fetching successful payments for seller: {}", sellerId);

        return paymentRepository.findSuccessfulPaymentsBySellerId(sellerId).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Calculer les gains totaux d'un vendeur
     */
    public BigDecimal calculateSellerEarnings(Long sellerId) {
        log.info("Calculating earnings for seller: {}", sellerId);

        BigDecimal total = paymentRepository.calculateTotalEarningsBySellerId(sellerId);
        return total != null ? total : BigDecimal.ZERO;
    }

    /**
     * Rembourser un paiement
     */
    public PaymentResponseDTO refundPayment(Long paymentId, RefundRequestDTO dto) {
        log.info("Refunding payment: {}", paymentId);

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + paymentId));

        if (payment.getStatus() != PaymentStatus.SUCCEEDED) {
            throw new InvalidOperationException("Only successful payments can be refunded");
        }

        try {
            // TODO: Intégration avec Stripe API pour remboursement
            // Pour l'instant, simulation

            payment.setStatus(PaymentStatus.REFUNDED);
            payment.setRefundedAt(LocalDateTime.now());
            payment.setErrorMessage("Refunded: " + dto.getReason());

            Order order = payment.getOrder();
            order.setStatus(OrderStatus.REFUNDED);
            orderRepository.save(order);

            log.info("Payment refunded successfully");

        } catch (Exception e) {
            log.error("Refund failed: {}", e.getMessage());
            throw new InvalidOperationException("Refund failed: " + e.getMessage());
        }

        payment = paymentRepository.save(payment);
        return mapToResponseDTO(payment);
    }

    /**
     * Annuler un paiement en attente
     */
    public PaymentResponseDTO cancelPayment(Long paymentId) {
        log.info("Cancelling payment: {}", paymentId);

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + paymentId));

        if (payment.getStatus() != PaymentStatus.PENDING && payment.getStatus() != PaymentStatus.PROCESSING) {
            throw new InvalidOperationException("Only pending or processing payments can be cancelled");
        }

        payment.setStatus(PaymentStatus.CANCELLED);
        payment.setErrorMessage("Cancelled by user");

        payment = paymentRepository.save(payment);
        log.info("Payment cancelled successfully");

        return mapToResponseDTO(payment);
    }

    /**
     * Compter les paiements par statut
     */
    public long countPaymentsByStatus(PaymentStatus status) {
        return paymentRepository.countByStatus(status);
    }

    private String generatePaymentIntentId() {
        return "pi_" + UUID.randomUUID().toString().replace("-", "").substring(0, 24);
    }

    private PaymentResponseDTO mapToResponseDTO(Payment payment) {
        Order order = payment.getOrder();
        Person buyer = order.getBuyer();

        return PaymentResponseDTO.builder()
                .id(payment.getId())
                .orderId(order.getId())
                .orderNumber(order.getOrderNumber())
                .paymentIntentId(payment.getPaymentIntentId())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .paymentMethod(payment.getPaymentMethod())
                .status(payment.getStatus())
                .stripeChargeId(payment.getStripeChargeId())
                .errorMessage(payment.getErrorMessage())
                .createdAt(payment.getCreatedAt())
                .processedAt(payment.getProcessedAt())
                .refundedAt(payment.getRefundedAt())
                .buyerName(buyer.getFirstName() + " " + buyer.getLastName())
                .buyerEmail(buyer.getEmail())
                .build();
    }
}

