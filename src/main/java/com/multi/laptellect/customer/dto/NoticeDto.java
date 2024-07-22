package com.multi.laptellect.customer.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@Builder
public class NoticeDto {
    private int noticeNo;
    private String title;
    private String writer;
//    private Timestamp createDate;
//    private Timestamp updateDate;
    private String createDate;
    private String updateDate;
    private String division;
    private String mainRegist;
    private String content;
    private String referenceCode;
}
