package com.neueda.leap.merchantportal;

import org.springframework.web.bind.annotation.*;

@RestController
public class WebhookController {

    private static final String WEBHOOK_SHARED_SECRET = System.getenv("PAYMENT_WEBHOOK_SECRET");

    private PayoutStatusUpdater payoutStatusUpdater;

    // FIX (A08): verify an HMAC signature
    @PostMapping("/api/webhooks/payment-status")
    public void handlePaymentStatusWebhook(
            @RequestBody PaymentStatusEvent event,
            @RequestHeader("X-Payment-Signature") String providedSignature,
            @RequestBody(required = false) String rawBody) {

        String expectedSignature = HmacUtil.sign(rawBody, WEBHOOK_SHARED_SECRET);
        if (!HmacUtil.constantTimeEquals(expectedSignature, providedSignature)) {
            throw new SecurityException("Invalid webhook signature");
        }

        payoutStatusUpdater.markSettled(event.getPayoutId(), event.getStatus());
    }
}
