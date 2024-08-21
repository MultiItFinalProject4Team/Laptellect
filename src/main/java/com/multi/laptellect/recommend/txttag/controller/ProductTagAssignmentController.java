//package com.multi.laptellect.recommend.txttag.controller;
//
//import com.multi.laptellect.recommend.txttag.service.RecommenService;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.context.event.ApplicationReadyEvent;
//import org.springframework.context.event.EventListener;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//
//@RestController
//@RequestMapping("/api/product-tags")
//@Slf4j
//public class ProductTagAssignmentController {
//
//    private final RecommenService recommenService;
//    private boolean isSchedulerRunning = false;
//
//    @Autowired
//    public ProductTagAssignmentController(RecommenService recommenService) {
//        this.recommenService = recommenService;
//    }
//
//    @EventListener(ApplicationReadyEvent.class)
//    public void onApplicationReady() {
//        log.info("Automatic product tag assignment started");
//        try {
//            recommenService.assignTagsToProducts();
//            log.info("Automatic product tag assignment completed successfully");
//        } catch (Exception e) {
//            log.error("Error during automatic product tag assignment: {}", e.getMessage());
//        }
//    }
//
//    }
