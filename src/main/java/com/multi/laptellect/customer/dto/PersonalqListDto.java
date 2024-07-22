package com.multi.laptellect.customer.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
public class PersonalqListDto {
    private int no;
    private String title;
    private String createdAt;
    //private Timestamp createdAt
}
