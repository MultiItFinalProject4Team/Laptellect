package com.multi.laptellect.product.model.dto.laptop;

import lombok.Data;

/**
 * Please explain the class!!
 *
 * @author : 이우석
 * @fileName : CPU
 * @since : 2024-07-30
 */
@Data
public class CPU {

    private String cpuManufacturer; //CPU 제조사
    private String cpuType; //CPU 종류
    private String cpuCodeName; //CPU 코드명
    private String cpuNumber; //CPU 넘버
    private String cpuCore; // 코어 수
    private String cpuThread; //스레드 수
    private String npu;
    private String npuTops;
}
