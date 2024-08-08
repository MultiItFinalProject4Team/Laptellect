package com.multi.laptellect.product.model.dto.laptop;

import lombok.Data;

/**
 * Please explain the class!!
 *
 * @fileName      :
 * @author        : 이우석
 * @since         :
 */
@Data
public class LaptopSpecDTO {
    // 상품 기본 정보
    private int productNo;
    private String productName; // 상품 명
    private String price; // 가격
    private String image; // 이미지
    private String productCode; // 다나와 코드
    private String registrationDate; // 등록 년월

    // 부가 정보

    private String os; // 운영체제
    private String company; // 제조사

    // 제품 스펙
    private CPU cpu;
    private GPU gpu;
    private RAM ram;
    private Display display;
    private Storage storage;
    private Power power;
    private Portability portability;
    private AddOn addOn;



}
