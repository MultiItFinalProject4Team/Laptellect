package com.multi.laptellect.recommend.clovaapi.model.dto;


import lombok.Data;

@Data
public class SentimentDTO {

    private int product_no;
    private int sentiment_positive;
    private int sentiment_denial;
    private int sentiment_neutrality;
}