package com.multi.laptellect.payment.model.dto;

import lombok.Data;

@Data
public class PaymentReviewDTO {
    private int paymentProductReivewsNo;
    private int memberNo;
    private int productNo;
    private char tagAnswer;
    private String content;
    private String rating;
    private String imPortId;
    private String createdAt;
    private String modifyAt;
    private String productName;
    private String userName;
}
