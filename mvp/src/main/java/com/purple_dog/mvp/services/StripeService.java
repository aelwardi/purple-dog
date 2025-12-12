package com.purple_dog.mvp.services;

import com.purple_dog.mvp.config.StripeConfig;
import com.purple_dog.mvp.dao.OrderRepository;
import com.purple_dog.mvp.dao.PaymentRepository;
import com.purple_dog.mvp.dao.PersonRepository;
import com.purple_dog.mvp.dao.StripeCustomerRepository;
import com.purple_dog.mvp.dao.ProductRepository;
import com.purple_dog.mvp.dao.QuickSaleRepository;
import com.purple_dog.mvp.dao.AuctionRepository;
import com.purple_dog.mvp.dto.*;
import com.purple_dog.mvp.entities.*;
import com.purple_dog.mvp.exceptions.ResourceNotFoundException;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.Customer;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import com.stripe.net.Webhook;
import com.stripe.exception.SignatureVerificationException;
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
    private final NotificationService notificationService;
    private final InAppNotificationService inAppNotificationService;
    private final ProductRepository productRepository;
    private final QuickSaleRepository quickSaleRepository;
    private final AuctionRepository auctionRepository;

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
                // Vérifier que l'ordre existe sans stocker une variable inutile
                if (!orderRepository.existsById(request.getOrderId())) {
                    throw new ResourceNotFoundException("Order not found");
                }
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

            // Fallback: if payment.order is null, try to link it using metadata.orderId
            if (payment.getOrder() == null) {
                String orderIdMeta = paymentIntent.getMetadata() != null ? paymentIntent.getMetadata().get("orderId") : null;
                if (orderIdMeta != null && !orderIdMeta.isBlank()) {
                    try {
                        Long orderId = Long.parseLong(orderIdMeta);
                        Order order = orderRepository.findByIdWithDetails(orderId).orElse(null);
                        if (order != null) {
                            payment.setOrder(order);
                            paymentRepository.save(payment);
                            log.info("[StripeService] confirmPayment: linked payment {} to order {} using metadata", payment.getId(), orderId);
                        } else {
                            log.warn("[StripeService] confirmPayment: order {} not found for payment {}", orderId, payment.getId());
                        }
                    } catch (NumberFormatException nfe) {
                        log.warn("[StripeService] confirmPayment: invalid orderId metadata '{}' for payment {}", orderIdMeta, payment.getId());
                    }
                } else {
                    log.warn("[StripeService] confirmPayment: no orderId metadata available for payment {}", payment.getId());
                }
            }

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
        // Accès autorisé si l'utilisateur est le propriétaire ou un administrateur
        if (!(payment.getUser().getId().equals(currentUserId) || isAdmin())) {
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
            // Vérifier et construire l'événement Stripe à partir de la charge utile et de l'en-tête de signature
            String webhookSecret = stripeConfig.getWebhookSecret();
            Event event;
            try {
                event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
            } catch (SignatureVerificationException sve) {
                log.error("⚠️ Stripe webhook signature verification failed: {}", sve.getMessage());
                throw new RuntimeException("Invalid Stripe webhook signature");
            }

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
        String previousStatus = payment.getStatus() != null ? payment.getStatus().name() : null;
        payment.setStatus(mapStripeStatus(paymentIntent.getStatus()));

        log.debug("[StripeService] updatePaymentFromStripe: paymentId={}, intentStatus={}, mappedStatus={} previousStatus={}",
                payment.getId(), paymentIntent.getStatus(), payment.getStatus(), previousStatus);

        // Reload order with details if present
        if (payment.getOrder() != null) {
            Order fullOrder = orderRepository.findByIdWithDetails(payment.getOrder().getId()).orElse(null);
            if (fullOrder != null) {
                payment.setOrder(fullOrder);
                log.debug("[StripeService] Reloaded order with details: id={}, quickSale={}, auction={}",
                        fullOrder.getId(), fullOrder.getQuickSale() != null, fullOrder.getAuction() != null);
            } else {
                log.warn("[StripeService] Could not reload order {} with details", payment.getOrder().getId());
            }
        }

        if (payment.getOrder() != null) {
            Order order = payment.getOrder();
            log.debug("[StripeService] Associated order found: id={}, orderNumber={}, status={}",
                    order.getId(), order.getOrderNumber(), order.getStatus());
            if (order.getQuickSale() != null) {
                QuickSale qs = order.getQuickSale();
                log.debug("[StripeService] Order has quickSale id={}, productId={}", qs.getId(), qs.getProduct() != null ? qs.getProduct().getId() : null);
            }
            if (order.getAuction() != null) {
                Auction auc = order.getAuction();
                log.debug("[StripeService] Order has auction id={}, productId={}", auc.getId(), auc.getProduct() != null ? auc.getProduct().getId() : null);
            }
        } else {
            log.debug("[StripeService] No order associated with payment id={}", payment.getId());
        }

        if ("succeeded".equals(paymentIntent.getStatus())) {
            payment.setPaidAt(LocalDateTime.ofInstant(
                    Instant.ofEpochSecond(paymentIntent.getCreated()),
                    ZoneId.systemDefault()
            ));

            // Envoyer email de confirmation de paiement et mettre à jour le produit
            if (payment.getOrder() != null && !("SUCCEEDED".equals(previousStatus))) {
                try {
                    Person user = payment.getUser();
                    Order order = payment.getOrder();
                    notificationService.sendPaymentConfirmationEmail(payment, user, order);

                    // Reload order with details to ensure quickSale/auction are loaded
                    Order fullOrder = orderRepository.findByIdWithDetails(order.getId()).orElse(null);
                    if (fullOrder != null) {
                        // Marquer le produit comme vendu
                        log.info("[StripeService] Payment succeeded - attempting to mark product as sold for order id={} orderNumber={}",
                                fullOrder.getId(), fullOrder.getOrderNumber());
                        markProductAsSold(fullOrder);
                    } else {
                        log.warn("[StripeService] Could not reload order {} with details", order.getId());
                    }

                    // Mettre à jour le statut de la commande
                    try {
                        order.setStatus(OrderStatus.PAID);
                        orderRepository.save(order);
                        log.info("Order {} marked as PAID", order.getOrderNumber());
                    } catch (Exception e) {
                        log.warn("Failed to update order status to PAID for order {}: {}", order != null ? order.getId() : null, e.getMessage());
                    }

                    // Notifications in-app : informer le vendeur et l'acheteur
                    try {
                        if (order.getSeller() != null) {
                            inAppNotificationService.createPaymentReceivedNotification(
                                    order.getSeller().getId(),
                                    order.getOrderNumber(),
                                    order.getTotalAmount() != null ? order.getTotalAmount().doubleValue() : 0.0
                            );
                        }

                        // Notifier l'acheteur également
                        NotificationCreateDTO buyerNotif = NotificationCreateDTO.builder()
                                .userId(order.getBuyer().getId())
                                .type(NotificationType.PAYMENT_RECEIVED)
                                .title("✅ Paiement confirmé")
                                .message(String.format("Paiement reçu pour la commande %s", order.getOrderNumber()))
                                .linkUrl("/orders/" + order.getId())
                                .build();

                        inAppNotificationService.createNotification(buyerNotif);

                    } catch (Exception e) {
                        log.warn("Failed to create in-app notifications after payment succeeded: {}", e.getMessage());
                    }

                } catch (Exception e) {
                    log.error("Error sending payment confirmation email or updating product: {}", e.getMessage());
                }
            }
        } else if ("failed".equals(paymentIntent.getStatus()) || "canceled".equals(paymentIntent.getStatus())) {
            // Envoyer email d'échec de paiement
            if (payment.getOrder() != null && !("FAILED".equals(previousStatus)) && !("CANCELED".equals(previousStatus))) {
                try {
                    Person user = payment.getUser();
                    Order order = payment.getOrder();
                    String errorMessage = paymentIntent.getLastPaymentError() != null
                        ? paymentIntent.getLastPaymentError().getMessage()
                        : "Le paiement n'a pas pu être traité";
                    notificationService.sendPaymentFailedEmail(payment, user, order, errorMessage);

                    // Créer une notification in-app pour l'acheteur indiquant l'échec
                    try {
                        NotificationCreateDTO failNotif = NotificationCreateDTO.builder()
                                .userId(order.getBuyer().getId())
                                .type(NotificationType.PAYMENT_FAILED)
                                .title("❌ Paiement échoué")
                                .message(String.format("Le paiement pour la commande %s a échoué : %s", order.getOrderNumber(), errorMessage))
                                .linkUrl("/orders/" + order.getId())
                                .build();
                        inAppNotificationService.createNotification(failNotif);
                    } catch (Exception ne) {
                        log.warn("Failed to create in-app payment-failed notification: {}", ne.getMessage());
                    }

                } catch (Exception e) {
                    log.error("Error sending payment failed email: {}", e.getMessage());
                }
            }
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
            String intentId = paymentIntent.getId();
            log.info("[StripeService] webhook: payment_intent.succeeded received for {}", intentId);
            paymentRepository.findByStripePaymentIntentId(intentId)
                    .ifPresentOrElse(payment -> {
                        updatePaymentFromStripe(payment, paymentIntent);
                        log.info("Payment succeeded and processed for payment intent: {}", intentId);
                    }, () -> {
                        // Fallback: Payment record not found (race or DB issue). Try to recover using metadata -> orderId
                        try {
                            String orderIdMeta = paymentIntent.getMetadata() != null ? paymentIntent.getMetadata().get("orderId") : null;
                            log.warn("[StripeService] No Payment entity found for intent {}. metadata.orderId={}", intentId, orderIdMeta);
                            if (orderIdMeta != null && !orderIdMeta.isBlank()) {
                                try {
                                    Long orderId = Long.parseLong(orderIdMeta);
                                    Order order = orderRepository.findByIdWithDetails(orderId).orElse(null);
                                    if (order != null) {
                                        log.info("[StripeService] Fallback: marking order {} as PAID and product as SOLD based on payment intent metadata", orderId);
                                        try {
                                            markProductAsSold(order);
                                        } catch (Exception e) {
                                            log.error("[StripeService] Fallback: error marking product as sold for order {}: {}", orderId, e.getMessage(), e);
                                        }
                                        try {
                                            order.setStatus(OrderStatus.PAID);
                                            orderRepository.save(order);
                                            log.info("[StripeService] Fallback: order {} marked as PAID", orderId);
                                        } catch (Exception e) {
                                            log.warn("[StripeService] Fallback: failed to update order status to PAID for order {}: {}", orderId, e.getMessage());
                                        }
                                    } else {
                                        log.warn("[StripeService] Fallback: order with id {} not found", orderId);
                                    }
                                } catch (NumberFormatException nfe) {
                                    log.warn("[StripeService] Fallback: metadata.orderId is not a valid number: {}", orderIdMeta);
                                }
                            } else {
                                log.warn("[StripeService] Fallback: no orderId metadata available on payment intent {}");
                            }
                        } catch (Exception ex) {
                            log.error("[StripeService] Exception during fallback processing for intent {}: {}", intentId, ex.getMessage(), ex);
                        }
                    });
        }
    }

    /**
     * Handle payment intent failed event
     */
    private void handlePaymentIntentFailed(Event event) {
        PaymentIntent paymentIntent = (PaymentIntent) event.getDataObjectDeserializer().getObject().orElse(null);
        if (paymentIntent != null) {
            String intentId = paymentIntent.getId();
            log.info("[StripeService] webhook: payment_intent.failed received for {}", intentId);
            paymentRepository.findByStripePaymentIntentId(intentId)
                    .ifPresentOrElse(payment -> {
                        payment.setStatus(Payment.PaymentStatus.FAILED);
                        payment.setFailureMessage(paymentIntent.getLastPaymentError() != null ?
                                paymentIntent.getLastPaymentError().getMessage() : "Payment failed");
                        paymentRepository.save(payment);
                        log.info("Payment failed processed for intent: {}", intentId);
                    }, () -> {
                        // If payment entity not found, log and attempt light fallback using metadata
                        try {
                            String orderIdMeta = paymentIntent.getMetadata() != null ? paymentIntent.getMetadata().get("orderId") : null;
                            log.warn("[StripeService] No Payment entity found for failed intent {}. metadata.orderId={}", intentId, orderIdMeta);
                            if (orderIdMeta != null && !orderIdMeta.isBlank()) {
                                try {
                                    Long orderId = Long.parseLong(orderIdMeta);
                                    Order order = orderRepository.findById(orderId).orElse(null);
                                    if (order != null) {
                                        // create in-app notification for buyer
                                        try {
                                            NotificationCreateDTO failNotif = NotificationCreateDTO.builder()
                                                    .userId(order.getBuyer().getId())
                                                    .type(NotificationType.PAYMENT_FAILED)
                                                    .title("❌ Paiement échoué")
                                                    .message(String.format("Le paiement pour la commande %s a échoué", order.getOrderNumber()))
                                                    .linkUrl("/orders/" + order.getId())
                                                    .build();
                                            inAppNotificationService.createNotification(failNotif);
                                            log.info("[StripeService] Fallback: created payment-failed in-app notification for order {}", orderId);
                                        } catch (Exception e) {
                                            log.warn("[StripeService] Fallback: failed to create in-app notification for failed payment: {}", e.getMessage());
                                        }
                                    }
                                } catch (NumberFormatException nfe) {
                                    log.warn("[StripeService] Fallback: metadata.orderId is not a valid number: {}", orderIdMeta);
                                }
                            }
                        } catch (Exception ex) {
                            log.error("[StripeService] Exception during fallback processing for failed intent {}: {}", intentId, ex.getMessage(), ex);
                        }
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

    /**
     * Marquer le produit comme vendu après paiement confirmé
     */
    public void markProductAsSold(Order order) {
        if (order == null) {
            log.warn("[StripeService] markProductAsSold called with null order");
            return;
        }

        try {
            log.debug("[StripeService] markProductAsSold: orderId={}, orderNumber={} status={}", order.getId(), order.getOrderNumber(), order.getStatus());

            Product product = null;

            // Récupérer le produit depuis QuickSale ou Auction
            if (order.getQuickSale() != null) {
                Long qsId = order.getQuickSale().getId();
                log.debug("[StripeService] order.getQuickSale() present with id={}", qsId);
                if (qsId != null) {
                    QuickSale quickSale = quickSaleRepository.findByIdWithDetails(qsId)
                            .orElse(null);
                    if (quickSale == null) {
                        log.warn("[StripeService] QuickSale id {} not found via findByIdWithDetails. Trying fallback: findByProductId({})", qsId, qsId);
                        try {
                            quickSale = quickSaleRepository.findByProductId(qsId).orElse(null);
                            if (quickSale != null) {
                                log.info("[StripeService] Fallback found QuickSale by productId: quickSaleId={}, productId={}", quickSale.getId(), qsId);
                            }
                        } catch (Exception e) {
                            log.warn("[StripeService] Exception during quickSaleRepository.findByProductId fallback: {}", e.getMessage());
                        }
                    }

                    if (quickSale != null) {
                        product = quickSale.getProduct();
                        log.debug("[StripeService] Found QuickSale in DB id={}, productId={}", quickSale.getId(), product != null ? product.getId() : null);
                        // Marquer la vente rapide comme non disponible
                        quickSale.setIsAvailable(false);
                        quickSale.setSoldAt(LocalDateTime.now());
                        quickSaleRepository.save(quickSale);
                        log.info("✅ QuickSale {} marked as sold", quickSale.getId());
                    } else {
                        // Fallback: maybe the provided id was actually a product id -> try load product directly
                        log.warn("[StripeService] quickSaleRepository could not find quickSale for id {}. Trying to find product by id {} as fallback.", qsId, qsId);
                        try {
                            var fallbackProduct = productRepository.findById(qsId).orElse(null);
                            if (fallbackProduct != null) {
                                product = fallbackProduct;
                                log.info("[StripeService] Found product by fallback id {}: productId={}", qsId, product.getId());
                            } else {
                                log.warn("[StripeService] No product found by fallback id {}", qsId);
                            }
                        } catch (Exception e) {
                            log.warn("[StripeService] Exception during productRepository.findById fallback: {}", e.getMessage());
                        }
                    }
                } else {
                    log.warn("[StripeService] order.getQuickSale().getId() is null");
                }
            } else if (order.getAuction() != null) {
                Long aucId = order.getAuction().getId();
                log.debug("[StripeService] order.getAuction() present with id={}", aucId);
                if (aucId != null) {
                    Auction auction = auctionRepository.findById(aucId)
                            .orElse(null);
                    if (auction == null) {
                        log.warn("[StripeService] Auction id {} not found. Trying fallback: treat id as productId and find auction by product.", aucId);
                        try {
                            var fallbackProduct = productRepository.findById(aucId).orElse(null);
                            if (fallbackProduct != null) {
                                auction = auctionRepository.findByProduct(fallbackProduct).orElse(null);
                                if (auction != null) {
                                    log.info("[StripeService] Found Auction by product fallback: auctionId={}, productId={}", auction.getId(), fallbackProduct.getId());
                                }
                            }
                        } catch (Exception e) {
                            log.warn("[StripeService] Exception during auction fallback lookup: {}", e.getMessage());
                        }
                    }

                    if (auction != null) {
                        product = auction.getProduct();
                        log.debug("[StripeService] Found Auction in DB id={}, productId={}", auction.getId(), product != null ? product.getId() : null);
                        // Marquer l'enchère comme vendue
                        auction.setStatus(AuctionStatus.SOLD);
                        auctionRepository.save(auction);
                        log.info("✅ Auction {} marked as SOLD", auction.getId());
                    } else {
                        log.warn("[StripeService] auctionRepository.findById({}) returned null and no fallback auction found", aucId);
                    }
                } else {
                    log.warn("[StripeService] order.getAuction().getId() is null");
                }
            } else {
                log.warn("[StripeService] Order {} has neither quickSale nor auction association", order.getId());

                // Fallback: try to find quick sale by seller and productPrice
                try {
                    if (order.getSeller() != null && order.getProductPrice() != null) {
                        Optional<QuickSale> maybe = quickSaleRepository.findAvailableBySellerAndPrice(order.getSeller().getId(), order.getProductPrice());
                        if (maybe.isPresent()) {
                            QuickSale qs = maybe.get();
                            product = qs.getProduct();
                            qs.setIsAvailable(false);
                            qs.setSoldAt(LocalDateTime.now());
                            quickSaleRepository.save(qs);
                            log.info("[StripeService] Fallback found QuickSale id={} by seller {} and price {}, associated product {}", qs.getId(), order.getSeller().getId(), order.getProductPrice(), product != null ? product.getId() : null);
                        } else {
                            log.debug("[StripeService] Fallback: no quickSale found by seller {} and price {}", order.getSeller() != null ? order.getSeller().getId() : null, order.getProductPrice());
                        }
                    }
                } catch (Exception e) {
                    log.warn("[StripeService] Fallback quickSale lookup failed: {}", e.getMessage());
                }
             }

            // Mettre à jour le statut du produit à SOLD
            if (product != null) {
                log.debug("[StripeService] Updating product id={} status -> SOLD", product.getId());
                product.setStatus(ProductStatus.SOLD);
                product.setUpdatedAt(LocalDateTime.now());
                productRepository.save(product);
                log.info("✅ Product {} marked as SOLD after payment confirmation", product.getId());
            } else {
                log.warn("⚠️ Could not find product for order {} - product is null", order.getId());
            }

        } catch (Exception e) {
            log.error("❌ Error marking product as sold for order {}: {}", order.getId(), e.getMessage(), e);
            // Ne pas propager l'exception pour ne pas bloquer le paiement
        }
    }
}
