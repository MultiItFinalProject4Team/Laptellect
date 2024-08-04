package com.multi.laptellect.payment.model.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class VerificationRequestDTO {
    private String im_port_id;
    private BigDecimal amount;
    private String usedPoints;
}