package com.multi.laptellect.recommend.laptop.model.dto;

import lombok.Data;

@Data
public class LaptopDTO {

    private int productNo;
    private int typeNo;
    private String productName;
    private int price;
    private String referenceCode;
    private double weight;
    private double screenSize;
    private String brand;
    private String series;
}
