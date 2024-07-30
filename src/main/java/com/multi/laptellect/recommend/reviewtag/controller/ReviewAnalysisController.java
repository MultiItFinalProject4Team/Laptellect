package com.multi.laptellect.recommend.reviewtag.controller;

import com.multi.laptellect.recommend.reviewtag.service.ReviewAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/review-analysis")
public class ReviewAnalysisController {

    @Autowired
    private ReviewAnalysisService reviewAnalysisService;

    @PostMapping("/analyze")
    public String analyzeReviews(@RequestParam String url) {
        reviewAnalysisService.performFullAnalysis(url); // 리뷰 분석 수행
        return "Analysis completed successfully"; // 리뷰 분석이 완료되었습니다.
    }
}
