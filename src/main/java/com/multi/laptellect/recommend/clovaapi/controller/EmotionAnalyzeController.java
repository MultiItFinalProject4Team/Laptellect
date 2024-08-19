package com.multi.laptellect.recommend.clovaapi.controller;

import com.multi.laptellect.product.model.dto.ReviewDTO;
import com.multi.laptellect.recommend.clovaapi.model.dao.SentimentDAO;
import com.multi.laptellect.recommend.clovaapi.service.EmotionAnalyzeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
public class EmotionAnalyzeController {
    private final EmotionAnalyzeService emotionAnalyzeService;
    private final SentimentDAO sentimentDAO;

    private static final int MAX_ANALYSES_PER_PRODUCT = 50;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationEvent() {
        log.info("서버 시작 시 자동으로 감성 분석 실행 (상품당 최대 {} 회)", MAX_ANALYSES_PER_PRODUCT);
        List<ReviewDTO> reviews = sentimentDAO.getUnanalyzedReviews();
        log.info("서버 시작 시 자동으로 감성 분석 실행");
        emotionAnalyzeService.analyzeAllUnanlyzedReviews();
        log.info("모든 상품의 감성 분석 작업 완료");

        // 상품 번호별로 리뷰 그룹화
        Map<Integer, List<ReviewDTO>> reviewsByProduct = reviews.stream()
                .collect(Collectors.groupingBy(ReviewDTO::getProductNo));

        for (Map.Entry<Integer, List<ReviewDTO>> entry : reviewsByProduct.entrySet()) {
            int productNo = entry.getKey();
            List<ReviewDTO> productReviews = entry.getValue();

            log.info("상품 번호 {} 의 감성 분석 시작", productNo);
            int analysisCount = 0;

            for (ReviewDTO review : productReviews) {
                if (analysisCount >= MAX_ANALYSES_PER_PRODUCT) {
                    log.info("상품 번호 {} 의 최대 분석 횟수({})에 도달했습니다.", productNo, MAX_ANALYSES_PER_PRODUCT);
                    break;
                }

                log.info("감성 분석 시작 - 상품 번호: {}, 리뷰 내용: {}", productNo, review.getContent());
                HashMap<String, Object> result = emotionAnalyzeService.getAnalyzeResult(productNo, review);
                log.info("감성 분석 결과: {}", result);

                analysisCount++;
            }

            log.info("상품 번호 {} 의 총 {}개 리뷰에 대해 감성 분석을 완료했습니다.", productNo, analysisCount);
        }


        log.info("모든 상품의 감성 분석 작업 완료");

    }

    @GetMapping("/api/sentiment/{productNo}")
    public String getProductSentiment(@PathVariable int productNo) {
        return emotionAnalyzeService.analyzeSentiment(productNo);
    }
}