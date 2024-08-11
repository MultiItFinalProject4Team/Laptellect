package com.multi.laptellect.recommend.txttag.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class ProductDTO2 {
    private int productNo;
    private int typeNo;
    private String productName;
    private int price;
    private String productCode;
    private String referenceCode;

    private String gpu;
    private String cpu;
    private String screen;
    private String weight;
    private String screenSize;
    private String batteryCapacity;
    private List<String> tags; // 태그 정보 추가
    private String internet; // 인터넷 정보 추가
}
