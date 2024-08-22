package com.multi.laptellect.config.Scheduler;

import com.multi.laptellect.common.service.LogService;
import com.multi.laptellect.product.service.ProductService;
import com.multi.laptellect.recommend.clovaapi.service.EmotionAnalyzeService;
import com.multi.laptellect.recommend.txttag.service.RecommenService;
import com.multi.laptellect.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

/**
 * 스케쥴러 설정 클래스
 *
 * @author : 이강석
 * @fileName : SchedulerConfiguration
 * @since : 2024-08-09
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SchedulerConfiguration {
    private final RedisUtil redisUtil;
    private final LogService logService;
    private final ProductService productService;
    private final RecommenService recommenService;
    private final EmotionAnalyzeService emotionAnalyzeService;

    @Scheduled(fixedRate = 50000) // fixedRate(서버 작동하자마자 시작) fixedDelay(종료 시점부터 시작, 1000 = 1초)
    public void run() {
        log.info("서버 시작 스케쥴러 확인");
    }

    @Scheduled(fixedRate = 300000) // 5분 간격으로 방문자 수 카운트
    public void visitCount() {
        log.info("방문자 수 카운트 스케쥴러");
            String sessionKey = "Visit:" + "count";

        try {
            int count = Integer.parseInt(redisUtil.getData(sessionKey));
            logService.insertVisitCount(count);
            redisUtil.deleteData(sessionKey);
        } catch (Exception e) {
            log.error("방문자 없음");
        }

    }


    @Scheduled(fixedRate = 180000) // 3분 간격으로 상품 조회수 업데이트
    public void viewProductCount() {
        String key = "Visit:product";

        int visitCount = 0;

        log.debug("상품 조회수 업데이트 시작");
        try {
            Map<String, String> visitProducts = redisUtil.getAllHashData(key);
            Set<String> productNos = visitProducts.keySet();

            if (!visitProducts.isEmpty()) {
                for (String productNo : productNos) {
                    visitCount = Integer.parseInt(visitProducts.get(productNo));
                    log.info("상품 조회수 = {}, {}", productNo, visitCount);

                    productService.updateProductVisit(productNo, visitCount);
                    log.info("상품 조회수 업데이트 완료 = {}", visitCount);

                    redisUtil.deleteHashData(key, productNo);
                    log.info("상품 방문자 기록 삭제 완료 = {}", productNo);
                }
            }
        } catch (Exception e) {
            log.error("상품 조회수 업데이트 실패 = ", e);
        }
    }

    @Scheduled(cron = "0 0 1 * * ?")
    public void assignProductTags() {
        log.info("태그화 스케줄러 시작");
        try {
            recommenService.assignTagsToProducts();
            log.info("노트북 태그화 성공");
        } catch (Exception e) {
            log.error("노트북 태그화 에러 = {}", e.getMessage());
        }
    }


    @Scheduled(cron = "0 0 2 * * ?")
    public void analyzeSentiments() {
        log.info("감정 분석 스케줄러 시작");
        try {
            emotionAnalyzeService.analyzeAllUnanlyzedReviews();
            log.info("감정 분석 완료");
        } catch (Exception e) {
            log.error("감정 분석 에러 = {}", e.getMessage());
        }
    }
}
