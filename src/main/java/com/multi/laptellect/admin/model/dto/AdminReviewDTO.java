package com.multi.laptellect.admin.model.dto;

import lombok.Data;

@Data
public class AdminReviewDTO {
    private int payment_product_reviews_no;
    private String username;
    private String productName;
    private String content;
    private String rating;
    private String create_date;
    private String modify_date;


}
