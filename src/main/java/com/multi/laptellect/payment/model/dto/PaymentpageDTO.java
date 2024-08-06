package com.multi.laptellect.payment.model.dto;

import lombok.Data;

@Data
public class PaymentpageDTO {
    private String productName;
    private String productCode;
    private String typeNo;
    private int price;
    private String productInfo;
    private String image;
}
