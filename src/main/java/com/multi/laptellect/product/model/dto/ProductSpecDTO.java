package com.multi.laptellect.product.model.dto;

import lombok.Data;

@Data
public class ProductSpecDTO {

    private int specNo;
    private int productNo;
    private int categoryNo;
    private String optionValue;


//    spec_no INT NOT NULL AUTO_INCREMENT,
//    product_no INT NOT NULL,
//    category_no INT NOT NULL,
//    option_value VARCHAR(20) NULL,
}
