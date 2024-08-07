package com.multi.laptellect.payment.model.dto;

import lombok.Data;

@Data
public class OrderlistDTO {
    private String username;
    private String productName;
    private String date_created;
    private int purchasePrice;
    private String imPortId;

    private String refund;
    private String refund_date;

}
