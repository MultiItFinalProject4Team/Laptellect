package com.multi.laptellect.admin.model.dto;

import lombok.Data;

@Data
public class AdminOrderlistDTO {
    private int payment_no;
    private String username;
    private String productName;
    private int productPrice;
    private int purchasePrice;
    private String date_created;
    private String imPortId;
    private String refund;
    private String refund_date;


}
