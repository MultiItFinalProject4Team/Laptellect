package com.multi.laptellect.payment.model.dto;

import lombok.Data;

@Data
public class PaymentpointDTO {
    private int paymentPointNo;
    private int memberNo;
    private String imPortId;
    private String paymentPointChange;
    private String paymentPointInfo;
    private String usedPoints;
}
