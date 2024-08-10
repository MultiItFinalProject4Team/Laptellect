package com.multi.laptellect.recommend.txttag.model.dto;

import lombok.Data;

import java.security.Timestamp;

@Data
public class ProductDTO2 {
    private int productNo;
    private int typeNo;
    private String productName;
    private int price;
    private String productCode;
    private String referenceCode;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private String gpu;
    private String cpu;
    private String weight;
    private String screenSize;
    private String batteryCapacity;
}
