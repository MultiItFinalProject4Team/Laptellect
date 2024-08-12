package com.multi.laptellect.recommend.txttag.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class ProductDTO2 {
    private String price; //
    private String gpu;
    private List<String> cpu;
    private List<String> screen;
    private List<String> weight;
    private List<String> screenSize;
    private List<String> batteryCapacity;
    private List<String> tags; // 태그 정보 추가
    private List<String> internet; //
}
