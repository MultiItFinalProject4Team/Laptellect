package com.multi.laptellect.product.model.dto;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class ProductDTO {
    private int productNo;
    private String productCode;
    private int typeNo;
    private String productName;
    private int price;
    private String referenceCode;
    private String image;
    private Timestamp createdAt;
    private Timestamp updatedAt;

}

