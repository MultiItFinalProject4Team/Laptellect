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
    private int personalqNo;
    private int memberNo;
    private String personalqCategorycode;
    private String title;
    private String content;
}
