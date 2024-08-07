package com.multi.laptellect.payment.model.dto;

import lombok.Data;

@Data
public class PaymentDTO {
    private int paymentNo;
    private int memberNo;
    private int productNo;
    private int purchasePrice;
    private String createdAt;
    private String imPortId;
    private String refund;
    private String refundAt;

    // 조인 결과를 위한 필드
    private String userName;
    private String productName;
    private int productPrice;
}