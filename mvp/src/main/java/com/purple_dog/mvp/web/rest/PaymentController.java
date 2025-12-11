package com.purple_dog.mvp.web.rest;

import com.purple_dog.mvp.dto.*;
import com.purple_dog.mvp.services.StripeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Payment", description = "Payment management with Stripe v31.0.0")
@SecurityRequirement(name = "bearerAuth")
public class PaymentController {

    private final StripeService stripeService;

    /**
     * Create a payment intent
     * POST /api/payments/create-intent
     */
    @PostMapping("/create-intent")
    @PreAuthorize("hasAnyRole('INDIVIDUAL', 'PROFESSIONAL', 'ADMIN')")
    @Operation(summary = "Create a payment intent", description = "Create a Stripe payment intent for processing payment")
    public ResponseEntity<PaymentIntentResponseDTO> createPaymentIntent(@Valid @RequestBody CreatePaymentIntentDTO request) {
        log.info("Creating payment intent for amount: {} {}", request.getAmount(), request.getCurrency());
        PaymentIntentResponseDTO response = stripeService.createPaymentIntent(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Confirm a payment
     * POST /api/payments/confirm
     */
    @PostMapping("/confirm")
    @PreAuthorize("hasAnyRole('INDIVIDUAL', 'PROFESSIONAL', 'ADMIN')")
    @Operation(summary = "Confirm a payment", description = "Confirm a payment after processing by Stripe")
    public ResponseEntity<PaymentResponseDTO> confirmPayment(@Valid @RequestBody ConfirmPaymentDTO request) {
        log.info("Confirming payment: {}", request.getPaymentIntentId());
        PaymentResponseDTO response = stripeService.confirmPayment(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Get payment by ID
     * GET /api/payments/{id}
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('INDIVIDUAL', 'PROFESSIONAL', 'ADMIN')")
    @Operation(summary = "Get payment by ID", description = "Get payment details by ID")
    public ResponseEntity<PaymentResponseDTO> getPayment(@PathVariable Long id) {
        log.info("Getting payment: {}", id);
        PaymentResponseDTO response = stripeService.getPayment(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Get current user's payments
     * GET /api/payments/my-payments
     */
    @GetMapping("/my-payments")
    @PreAuthorize("hasAnyRole('INDIVIDUAL', 'PROFESSIONAL', 'ADMIN')")
    @Operation(summary = "Get user payments", description = "Get all payments for the current user")
    public ResponseEntity<Page<PaymentResponseDTO>> getUserPayments(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        log.info("Getting user payments");
        Page<PaymentResponseDTO> response = stripeService.getUserPayments(pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Refund a payment
     * POST /api/payments/refund
     */
    @PostMapping("/refund")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Refund a payment", description = "Refund a payment (Admin only)")
    public ResponseEntity<PaymentResponseDTO> refundPayment(@Valid @RequestBody RefundPaymentDTO request) {
        log.info("Refunding payment: {}", request.getPaymentIntentId());
        PaymentResponseDTO response = stripeService.refundPayment(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Stripe webhook endpoint
     * POST /api/payments/webhook
     */
    @PostMapping("/webhook")
    @Operation(summary = "Stripe webhook", description = "Handle Stripe webhook events")
    public ResponseEntity<Void> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {
        log.info("Received Stripe webhook");
        stripeService.handleWebhookEvent(payload, sigHeader);
        return ResponseEntity.ok().build();
    }
}

