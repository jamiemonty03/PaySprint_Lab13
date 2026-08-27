package com.neueda.leap.merchantportal;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RestController
public class WebhookController {

    private static final String WEBHOOK_SHARED_SECRET = System.getenv("PAYMENT_WEBHOOK_SECRET");

    private final ObjectMapper objectMapper = new ObjectMapper();

    private PayoutStatusUpdater payoutStatusUpdater;

    // FIX (A08): verify an HMAC signature over the raw body before trusting the payload
    @PostMapping("/api/webhooks/payment-status")
    public void handlePaymentStatusWebhook(
            @RequestHeader("X-Payment-Signature") String providedSignature,
            HttpServletRequest request) throws IOException {

        String rawBody = new String(request.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

        String expectedSignature = HmacUtil.sign(rawBody, WEBHOOK_SHARED_SECRET);
        if (!HmacUtil.constantTimeEquals(expectedSignature, providedSignature)) {
            throw new SecurityException("Invalid webhook signature");
        }

        PaymentStatusEvent event = objectMapper.readValue(rawBody, PaymentStatusEvent.class);
        payoutStatusUpdater.markSettled(event.getPayoutId(), event.getStatus());
    }
}
