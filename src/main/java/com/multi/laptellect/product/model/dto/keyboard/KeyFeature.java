package com.multi.laptellect.product.model.dto.keyboard;

import lombok.Data;

/**
 * Please explain the class!!
 *
 * @author : 이우석
 * @fileName : KeyFeature
 * @since : 2024-08-10
 */
@Data
public class KeyFeature {

    private String nKeyRollover; // 동시입력 (무한 동시 입력)
    private String keycapMaterial; // 키캡 재질
    private String responseRate; // 응답속도
    private String keycapEngravingMethod; // 키캡 각인방식
    private String engravingPosition; // 각인 위치
}
