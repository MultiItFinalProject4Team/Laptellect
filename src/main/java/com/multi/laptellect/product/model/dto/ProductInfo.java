package com.multi.laptellect.product.model.dto;

import lombok.Data;

@Data
public class ProductInfo {

    private String productName;
    private String registrationMonth;
    private String price;
    private String categoryCode;
    private String pcode;
    private String imageUrl;
    private String cate3;
    private ProductTypeDTO productTypeDTO;
}
