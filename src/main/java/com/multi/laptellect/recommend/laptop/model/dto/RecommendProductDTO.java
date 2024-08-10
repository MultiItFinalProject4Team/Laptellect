package com.multi.laptellect.recommend.laptop.model.dto;

import lombok.Data;
import java.util.List;

@Data
public class RecommendProductDTO {
    private int productNo;
    private String productName;
    private int price;
    private String productCode;
    private String referenceCode;
    private List<String> tags;
    private String imageUrl;
    private String os;
    private String manufacturer;
    private String ramSize;
    private String cpuNumber;
    private String coreCount;
    private String storageSize;
    private String screenSize;
    private String resolution;
    private String gpuType;
}
