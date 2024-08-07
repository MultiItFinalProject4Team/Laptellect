package com.multi.laptellect.recommend.reviewtag.controller;

import com.multi.laptellect.recommend.reviewtag.service.ReviewAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/review-analysis")
public class ReviewAnalysisController {

    @Autowired
    private ReviewAnalysisService reviewAnalysisService;

    @PostMapping("/analyze")
    public String analyzeReviews() {
        reviewAnalysisService.performFullAnalysis();
        return "Analysis completed successfully";
    }
}