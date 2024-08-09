package com.multi.laptellect.payment.model.dto;

import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.Date;

@Data
public class PaymentDTO {
    private int paymentNo;
    private int memberNo;
    private int productNo;
    private int purchasePrice;
    private Date createdAt;
    private String imPortId;
    private String confirm;
    private String confirmAt;
    private String refund;
    private Date refundAt;

    // 조인 결과를 위한 필드
    private String userName;
    private String productName;
    private int productPrice;

    public String getFormatCreatedAt() {
        SimpleDateFormat formatCreateAt = new SimpleDateFormat("yyyy.MM.dd");
        return formatCreateAt.format(createdAt);
    }

    public String getFormatConfirm() {
        return this.confirm != null && this.confirm.equals("Y") ? "구매확정" : "결제완료";
    }
}