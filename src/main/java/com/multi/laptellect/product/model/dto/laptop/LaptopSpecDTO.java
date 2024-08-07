package com.multi.laptellect.product.model.dto.laptop;

import com.multi.laptellect.product.model.dto.SpecDTO;
import lombok.Data;

import java.util.List;

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
    private String productName;
    private String price;
    private String image;
    private String productCode;
    private String os;

    // NPU
    private String npu;
    private String nputops;
    private String soc;

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
