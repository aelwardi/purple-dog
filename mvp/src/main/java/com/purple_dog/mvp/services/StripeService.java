package com.purple_dog.mvp.services;

import com.purple_dog.mvp.config.StripeConfig;
import com.purple_dog.mvp.dao.OrderRepository;
import com.purple_dog.mvp.dao.PaymentRepository;
import com.purple_dog.mvp.dao.PersonRepository;
import com.purple_dog.mvp.dao.StripeCustomerRepository;
import com.purple_dog.mvp.dto.*;
import com.purple_dog.mvp.entities.Order;
import com.purple_dog.mvp.entities.Payment;
import com.purple_dog.mvp.entities.Person;
import com.purple_dog.mvp.entities.StripeCustomer;
import com.purple_dog.mvp.exceptions.ResourceNotFoundException;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.param.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class StripeService {

    private final PaymentRepository paymentRepository;
    private final StripeCustomerRepository stripeCustomerRepository;
    private final PersonRepository personRepository;
    private final OrderRepository orderRepository;
    private final StripeConfig stripeConfig;

    /**
     * Create a payment intent for a user
     */
    @Transactional
    public PaymentIntentResponseDTO createPaymentIntent(CreatePaymentIntentDTO request) {
        try {
            Long currentUserId = getCurrentUserId();
            Person user = personRepository.findById(currentUserId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            StripeCustomer stripeCustomer = getOrCreateStripeCustomer(user);

            long amountInCents = request.getAmount().multiply(BigDecimal.valueOf(100)).longValue();

            PaymentIntentCreateParams.Builder paramsBuilder = PaymentIntentCreateParams.builder()
                    .setAmount(amountInCents)
                    .setCurrency(request.getCurrency())
                    .setCustomer(stripeCustomer.getStripeCustomerId())
                    .setDescription(request.getDescription() != null ? request.getDescription() : "Purple Dog Payment")
                    .putMetadata("userId", currentUserId.toString());

            if (request.getOrderId() != null) {
                Order order = orderRepository.findById(request.getOrderId())
                        .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
                paramsBuilder.putMetadata("orderId", request.getOrderId().toString());
            }

            if (request.getPaymentMethodId() != null) {
                paramsBuilder.setPaymentMethod(request.getPaymentMethodId());
                paramsBuilder.setConfirmationMethod(PaymentIntentCreateParams.ConfirmationMethod.MANUAL);
            }

            paramsBuilder.setAutomaticPaymentMethods(
                    PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                            .setEnabled(true)
                            .build()
            );

            if (Boolean.TRUE.equals(request.getSavePaymentMethod())) {
                paramsBuilder.setSetupFutureUsage(PaymentIntentCreateParams.SetupFutureUsage.OFF_SESSION);
            }

            PaymentIntentCreateParams params = paramsBuilder.build();

            PaymentIntent paymentIntent = PaymentIntent.create(params);

            Payment payment = Payment.builder()
                    .stripePaymentIntentId(paymentIntent.getId())
                    .amount(request.getAmount())
                    .currency(request.getCurrency())
                    .status(Payment.PaymentStatus.PENDING)
                    .type(request.getOrderId() != null ? Payment.PaymentType.ORDER_PAYMENT : Payment.PaymentType.OTHER)
                    .user(user)
                    .order(request.getOrderId() != null ? orderRepository.findById(request.getOrderId()).orElse(null) : null)
                    .description(request.getDescription())
                    .stripeCustomerId(stripeCustomer.getStripeCustomerId())
                    .paymentMethodId(request.getPaymentMethodId())
                    .build();

            payment = paymentRepository.save(payment);

            log.info("Payment intent created: {} for user: {}", paymentIntent.getId(), currentUserId);

            return PaymentIntentResponseDTO.builder()
                    .clientSecret(paymentIntent.getClientSecret())
                    .paymentIntentId(paymentIntent.getId())
                    .amount(request.getAmount())
                    .currency(request.getCurrency())
                    .status(paymentIntent.getStatus())
                    .paymentId(payment.getId())
                    .publishableKey(stripeConfig.getPublishableKey())
                    .build();

        } catch (StripeException e) {
            log.error("Error creating payment intent: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create payment intent: " + e.getMessage());
        }
    }

    /**
     * Confirm a payment intent
     */
    @Transactional
    public PaymentResponseDTO confirmPayment(ConfirmPaymentDTO request) {
        try {
            PaymentIntent paymentIntent = PaymentIntent.retrieve(request.getPaymentIntentId());

            Payment payment = paymentRepository.findByStripePaymentIntentId(request.getPaymentIntentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

            updatePaymentFromStripe(payment, paymentIntent);

            log.info("Payment confirmed: {} with status: {}", paymentIntent.getId(), paymentIntent.getStatus());

            return mapToPaymentResponseDTO(payment);

        } catch (StripeException e) {
            log.error("Error confirming payment: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to confirm payment: " + e.getMessage());
        }
    }

    /**
     * Get payment by ID
     */
    public PaymentResponseDTO getPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

        Long currentUserId = getCurrentUserId();
        if (!payment.getUser().getId().equals(currentUserId) && !isAdmin()) {
            throw new RuntimeException("Access denied");
        }

        return mapToPaymentResponseDTO(payment);
    }

    /**
     * Get user payments with pagination
     */
    public Page<PaymentResponseDTO> getUserPayments(Pageable pageable) {
        Long currentUserId = getCurrentUserId();
        return paymentRepository.findByUserId(currentUserId, pageable)
                .map(this::mapToPaymentResponseDTO);
    }

    /**
     * Refund a payment
     */
    @Transactional
    public PaymentResponseDTO refundPayment(RefundPaymentDTO request) {
        try {
            Payment payment = paymentRepository.findByStripePaymentIntentId(request.getPaymentIntentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

            if (!isAdmin()) {
                throw new RuntimeException("Only admins can refund payments");
            }

            RefundCreateParams.Builder paramsBuilder = RefundCreateParams.builder()
                    .setPaymentIntent(request.getPaymentIntentId());

            if (request.getAmount() != null) {
                long amountInCents = request.getAmount().multiply(BigDecimal.valueOf(100)).longValue();
                paramsBuilder.setAmount(amountInCents);
            }

            if (request.getReason() != null) {
                paramsBuilder.setReason(RefundCreateParams.Reason.valueOf(request.getReason().toUpperCase()));
            }

            Refund refund = Refund.create(paramsBuilder.build());

            if (request.getAmount() != null && request.getAmount().compareTo(payment.getAmount()) < 0) {
                payment.setStatus(Payment.PaymentStatus.PARTIALLY_REFUNDED);
            } else {
                payment.setStatus(Payment.PaymentStatus.REFUNDED);
            }
            payment.setRefundedAt(LocalDateTime.now());
            paymentRepository.save(payment);

            log.info("Payment refunded: {} with refund ID: {}", request.getPaymentIntentId(), refund.getId());

            return mapToPaymentResponseDTO(payment);

        } catch (StripeException e) {
            log.error("Error refunding payment: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to refund payment: " + e.getMessage());
        }
    }

    /**
     * Handle Stripe webhook events
     */
    @Transactional
    public void handleWebhookEvent(String payload, String sigHeader) {
        try {
            Event event = Event.retrieve(payload);

            log.info("Received Stripe webhook event: {}", event.getType());

            switch (event.getType()) {
                case "payment_intent.succeeded":
                    handlePaymentIntentSucceeded(event);
                    break;
                case "payment_intent.payment_failed":
                    handlePaymentIntentFailed(event);
                    break;
                case "payment_intent.canceled":
                    handlePaymentIntentCanceled(event);
                    break;
                default:
                    log.info("Unhandled event type: {}", event.getType());
            }

        } catch (Exception e) {
            log.error("Error handling webhook event: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to handle webhook event: " + e.getMessage());
        }
    }
    /**
     * Get or create Stripe customer for a user
     */
    private StripeCustomer getOrCreateStripeCustomer(Person user) throws StripeException {
        // First, try to find existing customer
        Optional<StripeCustomer> existingCustomer = stripeCustomerRepository.findByUserId(user.getId());
        if (existingCustomer.isPresent()) {
            return existingCustomer.get();
        }

        // Customer doesn't exist, create a new one
        try {
            CustomerCreateParams params = CustomerCreateParams.builder()
                    .setEmail(user.getEmail())
                    .setName(user.getFirstName() + " " + user.getLastName())
                    .putMetadata("userId", user.getId().toString())
                    .build();

            Customer stripeCustomer = Customer.create(params);

            StripeCustomer customer = StripeCustomer.builder()
                    .user(user)
                    .stripeCustomerId(stripeCustomer.getId())
                    .email(user.getEmail())
                    .build();

            try {
                return stripeCustomerRepository.save(customer);
            } catch (Exception e) {
                // Handle race condition: another thread might have created the customer
                log.warn("Race condition detected while creating Stripe customer for user {}: {}", user.getId(), e.getMessage());
                // Try to fetch it again
                return stripeCustomerRepository.findByUserId(user.getId())
                        .orElseThrow(() -> new RuntimeException("Failed to create or retrieve Stripe customer"));
            }

        } catch (StripeException e) {
            log.error("Error creating Stripe customer: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create Stripe customer: " + e.getMessage());
        }
    }

    /**
     * Update payment from Stripe payment intent
     */
    private void updatePaymentFromStripe(Payment payment, PaymentIntent paymentIntent) {
        payment.setStatus(mapStripeStatus(paymentIntent.getStatus()));

        if ("succeeded".equals(paymentIntent.getStatus())) {
            payment.setPaidAt(LocalDateTime.ofInstant(
                    Instant.ofEpochSecond(paymentIntent.getCreated()),
                    ZoneId.systemDefault()
            ));
        }

        if (paymentIntent.getLatestCharge() != null) {
            try {
                Charge charge = Charge.retrieve(paymentIntent.getLatestCharge());
                payment.setReceiptUrl(charge.getReceiptUrl());
            } catch (StripeException e) {
                log.error("Error retrieving charge: {}", e.getMessage());
            }
        }

        paymentRepository.save(payment);
    }

    /**
     * Handle payment intent succeeded event
     */
    private void handlePaymentIntentSucceeded(Event event) {
        PaymentIntent paymentIntent = (PaymentIntent) event.getDataObjectDeserializer().getObject().orElse(null);
        if (paymentIntent != null) {
            paymentRepository.findByStripePaymentIntentId(paymentIntent.getId())
                    .ifPresent(payment -> {
                        updatePaymentFromStripe(payment, paymentIntent);
                        log.info("Payment succeeded: {}", paymentIntent.getId());
                    });
        }
    }

    /**
     * Handle payment intent failed event
     */
    private void handlePaymentIntentFailed(Event event) {
        PaymentIntent paymentIntent = (PaymentIntent) event.getDataObjectDeserializer().getObject().orElse(null);
        if (paymentIntent != null) {
            paymentRepository.findByStripePaymentIntentId(paymentIntent.getId())
                    .ifPresent(payment -> {
                        payment.setStatus(Payment.PaymentStatus.FAILED);
                        payment.setFailureMessage(paymentIntent.getLastPaymentError() != null ?
                                paymentIntent.getLastPaymentError().getMessage() : "Payment failed");
                        paymentRepository.save(payment);
                        log.info("Payment failed: {}", paymentIntent.getId());
                    });
        }
    }

    /**
     * Handle payment intent canceled event
     */
    private void handlePaymentIntentCanceled(Event event) {
        PaymentIntent paymentIntent = (PaymentIntent) event.getDataObjectDeserializer().getObject().orElse(null);
        if (paymentIntent != null) {
            paymentRepository.findByStripePaymentIntentId(paymentIntent.getId())
                    .ifPresent(payment -> {
                        payment.setStatus(Payment.PaymentStatus.CANCELED);
                        paymentRepository.save(payment);
                        log.info("Payment canceled: {}", paymentIntent.getId());
                    });
        }
    }

    /**
     * Map Stripe status to Payment status
     */
    private Payment.PaymentStatus mapStripeStatus(String stripeStatus) {
        return switch (stripeStatus) {
            case "succeeded" -> Payment.PaymentStatus.SUCCEEDED;
            case "processing" -> Payment.PaymentStatus.PROCESSING;
            case "requires_payment_method", "requires_confirmation", "requires_action" -> Payment.PaymentStatus.PENDING;
            case "canceled" -> Payment.PaymentStatus.CANCELED;
            default -> Payment.PaymentStatus.FAILED;
        };
    }

    /**
     * Map Payment to PaymentResponseDTO
     */
    private PaymentResponseDTO mapToPaymentResponseDTO(Payment payment) {
        return PaymentResponseDTO.builder()
                .id(payment.getId())
                .stripePaymentIntentId(payment.getStripePaymentIntentId())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .status(payment.getStatus())
                .type(payment.getType())
                .userId(payment.getUser().getId())
                .userEmail(payment.getUser().getEmail())
                .orderId(payment.getOrder() != null ? payment.getOrder().getId() : null)
                .description(payment.getDescription())
                .receiptUrl(payment.getReceiptUrl())
                .paidAt(payment.getPaidAt())
                .refundedAt(payment.getRefundedAt())
                .failureMessage(payment.getFailureMessage())
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .build();
    }

    /**
     * Get current user ID from security context
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User not authenticated");
        }

        // Extract user ID from authentication
        try {
            return Long.parseLong(authentication.getName());
        } catch (NumberFormatException e) {
            // If authentication name is email, find user by email
            String email = authentication.getName();
            Person person = personRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            return person.getId();
        }
    }

    /**
     * Check if current user is admin
     */
    private boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }
        return authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
    }
}

