package com.multi.laptellect.customer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class ProductSearchDto {
    private int  productNo;
    private int memberNo;
    private String category;
    private String keyword;
    private String answer;
    private String date;
    private String key;
    private String type;
}
