package com.multi.laptellect.recommend.laptop.model.dto;

import lombok.Data;

import java.util.List;

/**
 * 사용자가 원하는 상품 조건이 담긴 DTO
 *
 * @author : 이강석
 * @fileName : ProductFilterDTO
 * @since : 2024-08-08
 */
@Data
public class ProductFilterDTO {
    private List<String> gpu;
    private List<String> cpu;
    private List<String> place;
    private int minPrice;
    private int maxPrice; //
    private int minGamePrice;
    private int maxGamePrice; //
    private List<String> screen;
    private List<String> battery;
    private List<String> internet;

}
