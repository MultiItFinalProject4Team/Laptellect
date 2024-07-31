package com.multi.laptellect.payment.model.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class VerificationRequestDTO {
    private String impUid;
    private BigDecimal amount;
    private String usedPoints;
}