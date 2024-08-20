package com.multi.laptellect.payment.model.dto;

import lombok.Data;

@Data
public class PaymentpointDTO {
    private int paymentPointNo;
    private int memberNo;
    private String imPortId;
    private int paymentPointChange;
    private String paymentPointInfo;
    private String createdAt;

    private String usedPoints;
}
