package com.multi.laptellect.payment.model.dto;

import lombok.Data;

import java.text.DecimalFormat;

@Data
public class PaymentReviewDTO {
    private int paymentProductReviewsNo;
    private int memberNo;
    private int productNo;
    private char tagAnswer;
    private String content;
    private String rating;
    private String imPortId;
    private String createdAt;
    private String modifyAt;
    private String productName;
    private String memberName;
    private double avgRating;

    public void setAvgRating(double avgRating) {
        this.avgRating = avgRating;
    }

    public double getAvgRating() {
        DecimalFormat df = new DecimalFormat("#.#");
        return Double.parseDouble(df.format(avgRating));
    }
}
