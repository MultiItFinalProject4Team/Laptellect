package com.multi.laptellect.product.model.dto.mouse;

import lombok.Data;

/**
 * Please explain the class!!
 *
 * @author : 이우석
 * @fileName : MousePerformance
 * @since : 2024-08-18
 */
@Data
public class MousePerformance {

    private String maxDPI; // 최대 감도(DPI)
    private String responseTime; // 응답속도
    private String sensorType; // 센서
    private String switchType; // 스위치 방식
    private String mouseShape; // 마우스 형태

}
