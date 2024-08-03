package com.multi.laptellect.product.model.dto;

import lombok.Data;

/**
 * Please explain the class!!
 *
 * @author : 이우석
 * @fileName : ReviewDTO
 * @since : 2024-08-03
 */
@Data
public class ReviewDTO {
    private int productNo;
    private int rating;
    private String title;
    private String content;
}
