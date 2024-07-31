package com.multi.laptellect.customer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductqDto {
    private int productqNo;
    private int memberNo;
    private String productqCategoryCode;
    private int productNo;
    private String title;
    private String content;
    private Date createdAt;
    private Date updatedAt;
    private String answer;
    private String secret;
    private String referenceCode;
}
