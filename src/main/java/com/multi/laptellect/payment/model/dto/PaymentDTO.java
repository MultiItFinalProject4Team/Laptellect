package com.multi.laptellect.payment.model.dto;

import lombok.Data;


import java.time.LocalDateTime;


@Data
public class PaymentDTO {
    private int paymentNo;
    private int memberNo;
    private int productNo;
    private int purchasePrice;
    private LocalDateTime createdAt;
    private String imPortId;
    private String confirm;
    private String confirmAt;
    private String refund;
    private LocalDateTime refundAt;

    // 조인 결과를 위한 필드
    private String userName;
    private String productName;
    private int productPrice;

    public String getFormatCreatedAt() {
        SimpleDateFormat formatCreateAt = new SimpleDateFormat("yyyy.MM.dd");
        return formatCreateAt.format(createdAt);
    }

    public String getFormatConfirm() {
        return this.confirm.equals("Y") ? "구매확정" : "결제완료" ;
    }
}