package com.multi.laptellect.product.model.dto.laptop;

import lombok.Data;

/**
 * Please explain the class!!
 *
 * @author : 이우석
 * @fileName : Storage
 * @since : 2024-07-30
 */
@Data
public class Storage {

    private String storageCapacity; // 저장 용량
    private String storageType; // SSD, HDD, M2 등
    private String storageSlots; // 슬롯 개수
}
