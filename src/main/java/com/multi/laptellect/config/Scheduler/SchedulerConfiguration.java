package com.multi.laptellect.config.Scheduler;

import com.multi.laptellect.product.service.ProductService;
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
    private final ProductService productService;
    private final RecommenService recommenService;

//    @Scheduled(fixedRate = 50000) // fixedRate(서버 작동하자마자 시작) fixedDelay(종료 시점부터 시작, 1000 = 1초)
//    public void run() {
//        log.info("서버 시작 스케쥴러 확인");
//    }

    @Scheduled(fixedRate = 30000) // 5분 간격
    public void visitorCount() {
        log.info("방문자 수 카운트 스케쥴러");
    }

    @Scheduled(fixedRate = 18000) // 3분 간격
    public void viewProductCount() {
        String key = "Visit:product";

        int visitCount = 0;

        log.debug("상품 조회수 업데이트 시작");
        try {
            Map<String, String> visitProducts = redisUtil.getAllHashData(key);
            Set<String> productNos = visitProducts.keySet();

            if(!visitProducts.isEmpty()) {
                for(String productNo : productNos) {
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

}