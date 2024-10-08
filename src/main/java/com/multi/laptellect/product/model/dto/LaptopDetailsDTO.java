package com.multi.laptellect.product.model.dto;

import lombok.Data;

import java.util.List;

/**
 * Please explain the class!!
 *
 * @author : 이우석
 * @fileName : LaptopDetails
 * @since : 2024-08-01
 */
@Data
public class LaptopDetailsDTO {

    private int productNo;
    private String productName;
    private String productCode;
    private String categoryNo;
    private int typeNo;
    private int price;
    private String options;
    private String optionValue;
    private String uploadName;
    private String specsString;

    private List<SpecDTO> specs;
}
