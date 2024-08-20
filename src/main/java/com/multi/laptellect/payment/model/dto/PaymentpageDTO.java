package com.multi.laptellect.payment.model.dto;

import lombok.Data;

@Data
public class PaymentpageDTO {
    private int productNo;
    private String productName;
    private String productCode;
    private int typeNo;
    private int price;
    private String image;
}