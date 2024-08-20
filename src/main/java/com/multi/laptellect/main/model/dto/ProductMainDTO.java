package com.multi.laptellect.main.model.dto;

import lombok.Data;

import java.sql.Timestamp;

/**
 * Please explain the class!!
 *
 * @author : 이강석
 * @fileName : ProductMainDTO
 * @since : 2024-08-20
 */
@Data
public class ProductMainDTO {
    private int productNo;
    private String productName;
    private int price;
    private String image;
    private int avgScore;
    private int reviewCount;
    private Timestamp createdAt;
}
