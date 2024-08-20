package com.multi.laptellect.customer.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

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
    private Date createdAt;
    private Date updateAt;
    private String answer;
    private String referenceCode;
}
