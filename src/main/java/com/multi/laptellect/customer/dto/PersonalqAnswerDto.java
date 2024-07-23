package com.multi.laptellect.customer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonalqAnswerDto {
    private int personalaNo;
    private int personalqNo;
    private String title;
    private String content;
    private String createdAt;
    private String updatedAt;
    private String referenceCode;
}
