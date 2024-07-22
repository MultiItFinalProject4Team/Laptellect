package com.multi.laptellect.payment.model.dto;

public class VerificationRequestDto {
    private String impUid;
    private long amount;

    // Getters and setters
    public String getImpUid() {
        return impUid;
    }

    public void setImpUid(String impUid) {
        this.impUid = impUid;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }
}