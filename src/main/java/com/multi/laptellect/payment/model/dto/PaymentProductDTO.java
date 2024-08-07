package com.multi.laptellect.payment.model.dto;

import lombok.Data;

@Data
public class PaymentProductDTO {
    private String productName;
    private String productCode;
    private String typeNo;
    private String price;
    private String optionsInfo;  // options와 option_value를 합친 정보
    private String images;
}
