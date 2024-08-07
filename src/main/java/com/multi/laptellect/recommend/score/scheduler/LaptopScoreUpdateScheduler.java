package com.multi.laptellect.recommend.score.scheduler;

import com.multi.laptellect.recommend.score.service.LaptopScoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LaptopScoreUpdateScheduler {
    private final LaptopScoreService laptopScoreService;

    @Scheduled(cron = "0 0 1 * * ?") // 매일 새벽 1시에 실행
    public void updateLaptopScores() {
        laptopScoreService.calculateAndSaveLaptopScores();
    }
}
