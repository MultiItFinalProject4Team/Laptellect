package com.multi.laptellect.recommend.clovaapi.model.dto;


import lombok.Data;

@Data
public class SentimentDTO {
    private int productNo;
    private int sentimentPositive;
    private int sentimentDenial;
    private int sentimentNeutrality;
}