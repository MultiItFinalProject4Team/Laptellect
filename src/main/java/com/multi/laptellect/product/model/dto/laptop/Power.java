package com.multi.laptellect.product.model.dto.laptop;

import lombok.Data;

/**
 * Please explain the class!!
 *
 * @author : 이우석
 * @fileName : power
 * @since : 2024-07-30
 */
@Data
public class Power {
    private String battery; // 배터리 용량
    private String charging; // 전원 충전 타입 USB-PD 등
    private String adapter; // 어댑터 W
}
