package com.multi.laptellect.payment.model.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class VerificationRequestDTO {
    private String productName;
    private Long paymentNo;
    private String imPortId;
    private BigDecimal amount;
    private String usedPoints;
    private int addressId;
    private int quantity;
}