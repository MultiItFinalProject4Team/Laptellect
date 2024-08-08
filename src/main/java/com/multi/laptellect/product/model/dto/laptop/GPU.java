package com.multi.laptellect.product.model.dto.laptop;

import lombok.Data;

@Data
public class GPU {
    private String gpuType; //GPU 종류
    private String gpuManufacturer; //GPU 제조사
    private String gpuChipset; // GPU 칩셋
    private String gpuCore; // GPU 코어
    private String gpuClock; //GPU 클럭
}
