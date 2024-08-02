package com.multi.laptellect.customer.dto;

import lombok.*;

@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class PersonalqSearchDto {
    private int personalqNo;
    private int memberNo;
    private String category;
    private String keyword;
    private String answer;
    private String date;
}
