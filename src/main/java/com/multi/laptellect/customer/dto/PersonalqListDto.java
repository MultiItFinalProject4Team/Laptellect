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
public class PersonalqListDto {
    private int personalqNo;
    private int memberNo;
    private String title;
    private String answer;
    private Date createdAt;
    private String memberName;
}
