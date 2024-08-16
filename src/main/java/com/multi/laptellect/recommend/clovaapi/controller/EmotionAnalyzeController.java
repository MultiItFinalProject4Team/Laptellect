package com.multi.laptellect.recommend.clovaapi.controller;

import com.multi.laptellect.product.model.dto.ReviewDTO;
import com.multi.laptellect.recommend.clovaapi.service.EmotionAnalyzeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@Slf4j
@RestController
@RequiredArgsConstructor
public class EmotionAnalyzeController {
    private final EmotionAnalyzeService emotionAnalyzeService;

    @PostMapping("/analyze-sentiment")
    public HashMap<String, Object> analyzeSentiment(@RequestBody ReviewDTO reviewDTO) {
        log.info("감성 분석 요청: {}", reviewDTO);

        HashMap<String, Object> result = emotionAnalyzeService.getAnalyzeResult(reviewDTO.getProductNo(), reviewDTO);

        log.info("감성 분석 결과: {}", result);

        return result;
    }
}