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

    private String cpuManufacturer;
    private String cpuType;
    private String cpuCodeName;
    private String cpuNumber;
    private String cpuCore;
    private String cpuThread;
    private String npu;
    private String npuTops;
}
