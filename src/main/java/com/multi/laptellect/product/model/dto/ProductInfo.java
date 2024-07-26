package com.multi.laptellect.product.model.dto;

import lombok.Data;

@Data
public class ProductInfo {

    private String productName;
    private String referenceCode;
    private String price;
    private String categoryCode;
    private String productCode;
    private String imageUrl;
    private String cate3;
    private int typeNo;
    private ProductTypeDTO productTypeDTO;
}
