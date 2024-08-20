package com.multi.laptellect.product.model.dto.keyboard;

import lombok.Data;

/**
 * Please explain the class!!
 *
 * @author : 이우석
 * @fileName : KeyboardInfo
 * @since : 2024-08-10
 */
@Data
public class KeyboardSpecDTO {
    private int productNo;
    private int type_no;
    private String productName; // 상품 명
    private String price; // 가격
    private String image; // 이미지
    private String productCode; //다나와 코드


    private String manufacturer; // 제조사
    private String registrationDate; // 등록월
    private String size; // 사이즈
    private String connectionType; // 연결 방식
    private String interfaceType; // 인터페이스
    private String switchType; // 접점 방식



}
