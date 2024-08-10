package com.multi.laptellect.product.model.dto.keyboard;

import lombok.Data;

/**
 * Please explain the class!!
 *
 * @author : 이우석
 * @fileName : KeyDimensions
 * @since : 2024-08-10
 */
@Data
public class KeyDimensions {

    private String width; // 가로
    private String height; // 세로
    private String depth; // 높이
    private String weight; // 무게
    private String cableLength; // 케이블 길이
}
