package com.multi.laptellect.customer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProuductqListDto {
    private int productqNo;
    private int memberNo;
    private int productNo;
    private String title;
    private String secret;
    private String answer;
    private String createdAt;
}
