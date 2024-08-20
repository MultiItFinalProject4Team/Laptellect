package com.multi.laptellect.admin.model.dto;

import lombok.Data;

@Data
public class AdminReviewDTO {
    private int paymentProductReviewsNo;
    private int memberNo;
    private int productNo;
    private char tagAnswer;
    private String content;
    private String rating;
    private String memberName;
    private String imPortId;
    private String createdAt;
    private String modifyAt;
    private String productName;
}