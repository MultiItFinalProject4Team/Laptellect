//package com.multi.laptellect.recommend.txttag.scheduler;
//
//import com.multi.laptellect.recommend.txttag.service.RecommenService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//import lombok.extern.slf4j.Slf4j;
//
//@Component
//@Slf4j
//public class TagAssignmentScheduler {
//
//    private final RecommenService recommenService;
//
//    @Autowired
//    public TagAssignmentScheduler(RecommenService recommenService) {
//        this.recommenService = recommenService;
//    }
//
//    @Scheduled(fixedRate = 50000)  // 매일 새벽 1시에 실행
//    public void scheduleTagAssignment() {
//        log.info("태그 할당 스케줄러 시작");
//        try {
//            recommenService.assignTagsToProducts();
//            log.info("태그 할당 스케줄러 완료");
//        } catch (Exception e) {
//            log.error("태그 할당 중 오류: {}", e.getMessage());
//        }
//    }
//}