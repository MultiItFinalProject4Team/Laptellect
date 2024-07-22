package com.multi.laptellect.product.model.dto;

import lombok.Data;

@Data
public class ProductDTO {
    private String pcode;
    private String os;
    private String cpuManufacturer;
    private String cpuType;
    private String cpuCodeName;
    private String cpuNumber;
    private String gpuType;
    private String gpuManufacturer;
    private String gpuChipset;
    private String ramType;
    private String ramSize;
    private String screenSize;
    private String screenResolution;
    private String storageType;
    private String storageCapacity;
    private String convenienceFeatures;
    private String additionalFeatures;

}
