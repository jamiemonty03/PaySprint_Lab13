package com.neueda.leap.merchantportal;

public class PayoutApprovalService {

    private PayoutRepository payoutRepository;

    public PayoutApprovalService(PayoutRepository payoutRepository) {
        this.payoutRepository = payoutRepository;
    }

    // FIX (A06): this is a design fix
    public void approve(Long payoutId, Long approvingUserId) {
        PayoutRequest payout = payoutRepository.findById(payoutId)
                .orElseThrow(() -> new RuntimeException("Payout not found"));

        if (payout.getRequestedByUserId().equals(approvingUserId)) {
            throw new IllegalStateException(
                    "A payout cannot be approved by the same user who requested it");
        }

        payout.setApprovalStatus("APPROVED");
        payout.setApprovedByUserId(approvingUserId);
        payoutRepository.save(payout);
    }
}
