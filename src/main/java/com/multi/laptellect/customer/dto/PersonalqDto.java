package com.multi.laptellect.customer.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.sql.Timestamp;

@Getter
@Setter
@ToString
@Builder
public class PersonalqDto {
    private int personalqNo;
    private int memberNo;
    private String personalCategorycode;
    private String title;
    private String content;
    private Timestamp createdAt;
    private Timestamp updateAt;
    private String answer;
    private String referenceCode;
}
