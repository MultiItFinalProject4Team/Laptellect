package com.multi.laptellect.recommend.laptop.model.dto;

import lombok.Data;

@Data
public class RecommendationRequestDTO {
    private String usage;
    private String gameType;
    private String officeType;
    private String price;
    private String weight;
    private String screen;
    private String priority;


}