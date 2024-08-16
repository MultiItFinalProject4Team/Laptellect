package com.multi.laptellect.recommend.clovaapi.controller;

import com.multi.laptellect.product.model.dto.ReviewDTO;
import com.multi.laptellect.recommend.clovaapi.service.EmotionAnalyzeService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@Slf4j
@RestController
@RequiredArgsConstructor
public class EmotionAnalyzeController {
    private final EmotionAnalyzeService emotionAnalyzeService;

    @PostConstruct
    public void init() {
        log.info("서버 시작 시 자동으로 감성 분석을 수행합니다.");
        ReviewDTO testReview = new ReviewDTO();
        testReview.setContent("이 제품은 정말 좋아요! 매우 만족스럽습니다.");
        int productNo = 1; // 테스트용 제품 번호

        HashMap<String, Object> result = emotionAnalyzeService.getAnalyzeResult(productNo, testReview);
        log.info("감성 분석 결과: {}", result);
    }
}