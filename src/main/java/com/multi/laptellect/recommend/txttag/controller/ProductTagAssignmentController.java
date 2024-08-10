package com.multi.laptellect.recommend.txttag.controller;

import com.multi.laptellect.recommend.txttag.service.RecommenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/product-tags")
@Slf4j
public class ProductTagAssignmentController {

    private final RecommenService recommenService;
    private boolean isSchedulerRunning = false;

    @Autowired
    public ProductTagAssignmentController(RecommenService recommenService) {
        this.recommenService = recommenService;
    }

    @PostMapping("/assign")
    public ResponseEntity<String> assignTags() {
        log.info("Manual product tag assignment requested");
        try {
            recommenService.assignTagsToProducts();
            log.info("Manual product tag assignment completed successfully");
            return ResponseEntity.ok("Product tag assignment completed successfully");
        } catch (Exception e) {
            log.error("Error during manual product tag assignment: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("Error occurred during product tag assignment");
        }
    }

    @PostMapping("/toggle-scheduler")
    public ResponseEntity<String> toggleScheduler() {
        isSchedulerRunning = !isSchedulerRunning;
        String message = isSchedulerRunning ? "Scheduler started" : "Scheduler stopped";
        log.info(message);
        return ResponseEntity.ok(message);
    }
}