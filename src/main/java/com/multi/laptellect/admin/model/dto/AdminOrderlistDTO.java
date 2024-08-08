package com.multi.laptellect.admin.model.dto;

import lombok.Data;

@Data
public class AdminOrderlistDTO {
    private int paymentNo;
    private int memberNo;
    private int productNo;
    private String userName;
    private String productName;
    private int productPrice;
    private int purchasePrice;
    private String createdAt;
    private String imPortId;
    private String refund;
    private String refundAt;
}
