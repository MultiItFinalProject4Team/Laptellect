package com.multi.laptellect.payment.model.dto;

import lombok.Data;

@Data
public class PaymentpointDTO {
    private String username;
    private int possessionpoint;
    private String imd;
    private String pointchange;
    private String pointinfo;
    private String usedPoints;
}
