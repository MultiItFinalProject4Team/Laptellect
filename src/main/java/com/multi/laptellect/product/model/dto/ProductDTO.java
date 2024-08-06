package com.multi.laptellect.product.model.dto;

import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
public class ProductDTO {
    private int productNo;
    private String productCode;
    private int typeNo;
    private String productName;
    private int price;
    private String referenceCode;
    private String image;
    private String manufacturer;
    private String registrationMonth;
    private List<SpecDTO> specs;
    private String specsString;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private int quantity;
    private int totalPrice;

    public int getTotalPrice() {
        return this.price * this.quantity;
    }
}

