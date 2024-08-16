package com.multi.laptellect.payment.model.dto;

import lombok.Data;

@Data
public class ProductInfo {
    private String productName;
    private int quantity;
    private int price;
    private int totalPrice;
    private String image;
}
