package com.multi.laptellect.admin.model.dto;

import lombok.Data;

@Data
public class AdminOrderlistDTO {
    private int payment_no;
    private String username;
    private String productname;
    private String productinfo;
    private int productprice;
    private int purchaseprice;
    private String date_created;
    private String imPortId;

    private String refund;
    private String refund_date;


}
