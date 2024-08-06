package com.multi.laptellect.payment.model.dto;

import lombok.Data;

@Data
public class PaymentDTO {
    private int payment_no;
    private String username;
    private String productName;
    private int productPrice;
    private int purchasePrice;
    private String date_created;
    private String imPortId;


}
