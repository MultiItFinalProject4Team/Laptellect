package com.multi.laptellect.payment.model.dto;

import lombok.Data;

@Data
public class OrderlistDTO {
    private String username;
    private String productname;
    private String productinfo;
    private String date_created;
    private int purchaseprice;
    private String imd;
    private String refund;
    private String refund_date;

}
