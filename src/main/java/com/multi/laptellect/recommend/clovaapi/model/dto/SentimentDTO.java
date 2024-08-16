package com.multi.laptellect.recommend.clovaapi.model.dto;


import lombok.Data;

@Data
public class SentimentDTO {

    private int product_no;
    private double sentiment_positive;
    private double sentiment_denial;
    private double sentiment_neutrality;
}