package com.multi.laptellect.recommend.laptop.model.dto;

import lombok.Data;
import java.util.List;

@Data
public class RecommendProductDTO {
    private int productNo;
    private String productName;
    private int price;
    private String productCode;
    private String referenceCode;
    private List<String> tags;
}
