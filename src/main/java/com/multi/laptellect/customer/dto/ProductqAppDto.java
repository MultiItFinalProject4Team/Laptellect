package com.multi.laptellect.customer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductqAppDto {
    private int productqNo;
    private int memberNo;
    private int productNo;
    private String productqCategorycode;
    private String title;
    private String content;
    private String secret;
}
