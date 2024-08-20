package com.multi.laptellect.admin.model.dto;

import lombok.Data;

@Data
public class AdminDashboardDTO {
    private String date;
    private int visitCount;
    private int saleCount;
    private int newMemberCount;
    private int reviewCount;
    private int inquiryCount;
}
