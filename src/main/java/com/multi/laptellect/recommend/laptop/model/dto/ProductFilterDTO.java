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
public class ProductFilterDTO { //
    private List<String> gpu;
    private List<String> cpu;
    private List<String> place; // 무게
    private int minPrice; // 가격
    private int maxPrice; // 가격
    private int minGamePrice;  // 게이밍 가격
    private int maxGamePrice; // 게이밍 가격
    private List<String> screen;
    private List<String> battery;
    private List<String> internet;

}
