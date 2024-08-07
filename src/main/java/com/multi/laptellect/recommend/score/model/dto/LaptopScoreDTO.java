package com.multi.laptellect.recommend.score.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LaptopScoreDTO {
    private int productNo;
    private int cpuScore;
    private int gpuScore;
    private int ramScore;
    private int storageScore;
    private int priceScore;
    private int weightScore;
    private int screenSizeScore;
    private int resolutionScore;
}
