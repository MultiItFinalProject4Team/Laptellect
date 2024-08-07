package com.multi.laptellect.recommend.score.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetailsDTO {
    private int productNo;
    private int typeNo;
    private String productName;
    private int price;
    private String productCode;
    private Map<String, String> specs;
}