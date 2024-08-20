package com.multi.laptellect.recommend.reviewtag.controller;

import com.multi.laptellect.recommend.reviewtag.service.ReviewAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/review-analysis")
public class ReviewAnalysisController {
    private final ReviewAnalysisService reviewAnalysisService;

    @PostMapping("/analyze")
    public String analyzeReviews() {
        reviewAnalysisService.performFullAnalysis();
        return "Analysis completed successfully";
    }
}