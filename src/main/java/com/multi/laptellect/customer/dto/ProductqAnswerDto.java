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
public class ProductqAnswerDto {
    private int productaNo;
    private int productqNo;
    private int productNo;
    private String title;
    private String content;
    private Date createdAt;
    private Date updatedAt;
    private String referenceCode;
}
