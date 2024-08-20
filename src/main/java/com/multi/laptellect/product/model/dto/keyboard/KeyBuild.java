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
public class KeyBuild {

    private String keyLayout; // 키 배열
    private String switchType; // 스위치
    private String keySwitch; // 키 스위치
    private String switchMethod; // 스위치 방식

}
