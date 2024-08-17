//package com.multi.laptellect.recommend.clovaapi.controller;
//
//import com.multi.laptellect.product.model.dto.ReviewDTO;
//import com.multi.laptellect.recommend.clovaapi.service.EmotionAnalyzeService;
//import com.multi.laptellect.recommend.clovaapi.model.dao.SentimentDAO;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.context.event.ApplicationReadyEvent;
//import org.springframework.context.event.EventListener;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.List;
//import java.util.HashMap;
//
//@Slf4j
//@RestController
//@RequiredArgsConstructor
//public class EmotionAnalyzeController {
//    private final EmotionAnalyzeService emotionAnalyzeService;
//    private final SentimentDAO sentimentDAO;
//
//    @EventListener(ApplicationReadyEvent.class)
//    public void onApplicationEvent() {
//        log.info("서버 시작 시 자동으로 감성 분석 실행");
//        List<ReviewDTO> reviews = sentimentDAO.getUnanalyzedReviews();
//
//        for (ReviewDTO review : reviews) {
//            log.info("감성 분석 시작 - 상품 번호: {}, 리뷰 내용: {}", review.getProductNo(), review.getContent());
//            HashMap<String, Object> result = emotionAnalyzeService.getAnalyzeResult(review.getProductNo(), review);
//            log.info("감성 분석 결과: {}", result);
//        }
//
//        log.info("모든 리뷰의 감성 분석 완료");
//    }
//}