package com.multi.laptellect.customer.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
public class PersonalqDto {
    private int personalqNo;
    private String title;
    private String writer;
    //    private Timestamp createDate;
//    private Timestamp updateDate;
    private String createDate;
    private String content;
}
