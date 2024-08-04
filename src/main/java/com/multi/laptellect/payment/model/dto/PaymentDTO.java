package com.multi.laptellect.payment.model.dto;

import lombok.Data;

@Data
public class PaymentDTO {
    private int payment_no;
    private String username;
    private String productname;
    private String productinfo;
    private int productprice;
    private int purchaseprice;
    private String date_created;
    private String imPortId;


}
