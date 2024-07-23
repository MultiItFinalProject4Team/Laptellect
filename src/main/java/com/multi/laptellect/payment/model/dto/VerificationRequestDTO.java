package com.multi.laptellect.payment.model.dto;

import java.math.BigDecimal;

public class VerificationRequestDTO {
    private String impUid;
    private BigDecimal amount;

    public String getImpUid() {
        return impUid;
    }

    public void setImpUid(String impUid) {
        this.impUid = impUid;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}