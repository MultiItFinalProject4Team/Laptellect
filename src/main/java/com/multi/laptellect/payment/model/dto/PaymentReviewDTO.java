package com.multi.laptellect.payment.model.dto;

import lombok.Data;

@Data
public class PaymentReviewDTO {
    private String productName;
    private String username;
    private String rating;
    private String content;
    private String impUid;
}
