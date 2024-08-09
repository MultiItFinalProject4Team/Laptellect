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
//    @Scheduled(cron = "0 0 1 * * ?")  // 매일 새벽 1시에 실행
//    public void scheduleTagAssignment() {
//        log.info("Starting scheduled tag assignment");
//        try {
//            recommenService.assignTagsToProducts();
//            log.info("Scheduled tag assignment completed successfully");
//        } catch (Exception e) {
//            log.error("Error during scheduled tag assignment: {}", e.getMessage());
//        }
//    }
//}