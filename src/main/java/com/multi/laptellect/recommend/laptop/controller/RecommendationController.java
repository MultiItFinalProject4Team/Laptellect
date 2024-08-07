package com.multi.laptellect.recommend.laptop.controller;

import com.multi.laptellect.recommend.laptop.model.dto.RecommendProductDTO;
import com.multi.laptellect.recommend.laptop.service.RecommendProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Controller
public class RecommendationController {

    private static final Logger logger = LoggerFactory.getLogger(RecommendationController.class);

    @Autowired
    private RecommendProductService recommendProductService;

    @GetMapping("/recommend")
    public String showRecommendationForm() {
        return "recommend/recommend";
    }

    @PostMapping("/recommendpage")
    public String getRecommendations(@RequestParam Map<String, String> surveyResults, Model model) {
        logger.info("Received survey results: {}", surveyResults);
        try {
            List<RecommendProductDTO> recommendations = recommendProductService.getRecommendations(surveyResults);
            logger.info("Found {} recommended products", recommendations.size());
            model.addAttribute("recommendations", recommendations);
            model.addAttribute("surveyResults", surveyResults);  // 추가: 설문 결과를 모델에 추가
            return "recommend/recommendpage";
        } catch (Exception e) {
            logger.error("Error while processing recommendations", e);
            model.addAttribute("error", "An error occurred while processing your request.");
            return "error";
        }
    }
}