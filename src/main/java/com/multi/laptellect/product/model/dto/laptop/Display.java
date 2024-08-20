package com.multi.laptellect.product.model.dto.laptop;

import lombok.Data;

/**
 * Please explain the class!!
 *
 * @author : 이우석
 * @fileName : Display
 * @since : 2024-07-30
 */
@Data
public class Display {
    private String screenSize; // 화면 크기
    private String resolution; // 해상도
    private String panelSurface; // 패널 표면 처리(안티글래어 등)
    private String refreshRate; // 화면 주사율 FPS
    private String brightness; // 화면 밝기
    private String panelType; // 패널 종류
}

