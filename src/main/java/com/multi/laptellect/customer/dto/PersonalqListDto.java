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
public class PersonalqListDto {
    private int personalqNo;
    private int memberNo;
    private String title;
    private String answer;
    private Timestamp createdAt;
}
