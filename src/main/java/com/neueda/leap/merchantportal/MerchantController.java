package com.neueda.leap.merchantportal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class MerchantController {

    @Autowired
    private PayoutRepository payoutRepository;

    @GetMapping("/api/payouts/{payoutId}")
    public PayoutRequest getPayout(@PathVariable Long payoutId,
                                    @RequestHeader("X-Merchant-Id") Long callerMerchantId) {
        PayoutRequest payout = payoutRepository.findById(payoutId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (!payout.getMerchantId().equals(callerMerchantId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        return payout;
    }
}
