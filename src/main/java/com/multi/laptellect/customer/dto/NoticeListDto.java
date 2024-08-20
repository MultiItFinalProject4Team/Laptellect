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
public class NoticeListDto {
    private int noticeNo;
    private int memberNo;
    private String title;
    private String content;
    private String mainRegist;
    private Date createdAt;
    private Date updatedAt;
    private String referenceCode;
}
