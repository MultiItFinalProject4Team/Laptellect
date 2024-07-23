package com.multi.laptellect.recommend.laptop.model.dto;

import lombok.Data;
//임의로 생성 나중에 수정
@Data
public class LaptopDTO {
    private Long id;
    private String model;
    private String brand;
    private String cpu;
    private String gpu;
    private int ram;
    private String storage;
    private String display;
    private double price;
    private String imageUrl;
    private String tags;
}
