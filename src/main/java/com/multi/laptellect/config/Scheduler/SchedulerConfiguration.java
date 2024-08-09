package com.multi.laptellect.config.Scheduler;

import com.multi.laptellect.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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

    @Scheduled(fixedRate = 50000) // fixedRate(서버 작동하자마자 시작) fixedDelay(종료 시점부터 시작, 1000 = 1초)
    public void run() {
        log.info("서버 시작 스케쥴러 확인");
    }

//    @Scheduled(fixedRate = 50000)
//    public void visitorCount() {
//        log.info("서버 시작 스케쥴러 확인");
//    }
//
//    @Scheduled(fixedRate = 50000)
//    public void viewProductCount() {
//        log.info("서버 시작 스케쥴러 확인");
//    }
}