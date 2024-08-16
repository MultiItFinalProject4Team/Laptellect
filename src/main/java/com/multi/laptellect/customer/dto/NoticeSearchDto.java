package com.multi.laptellect.customer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoticeSearchDto {
    private String keyword;
    private String mainRegist;
    private String date;
}
