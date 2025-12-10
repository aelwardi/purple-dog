package com.purple_dog.mvp.services;

/**
 * @deprecated This service has been completely replaced by StripeService.
 *
 * The old Payment entity structure was incompatible with Stripe v31.0.0.
 * All payment functionality is now handled by:
 * - StripeService: Complete Stripe integration
 * - PaymentController: REST endpoints
 *
 * Migration:
 * - Old: PaymentService.createPayment() → New: StripeService.createPaymentIntent()
 * - Old: PaymentService.processPayment() → New: StripeService.confirmPayment()
 * - Old: PaymentService.refundPayment() → New: StripeService.refundPayment()
 *
 * This file is kept only as a placeholder to avoid import errors.
 * DO NOT USE THIS CLASS.
 *
 * @see com.purple_dog.mvp.services.StripeService
 * @see com.purple_dog.mvp.web.rest.PaymentController
 */
@Deprecated
public class PaymentService {

    private PaymentService() {
        throw new UnsupportedOperationException(
            "PaymentService is deprecated. Use StripeService instead."
        );
    }
}

