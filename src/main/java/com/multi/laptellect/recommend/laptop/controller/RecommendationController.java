package com.multi.laptellect.recommend.laptop.controller;

import com.multi.laptellect.recommend.laptop.model.dto.RecommendProductDTO;
import com.multi.laptellect.recommend.laptop.service.RecommendProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/recommend")
public class RecommendationController {

    private static final Logger logger = LoggerFactory.getLogger(RecommendationController.class);

    @Autowired
    private RecommendProductService recommendProductService;

    @PostMapping
    public String getRecommendations(@RequestBody Map<String, String> surveyResults, Model model) {
        logger.info("Received survey results: {}", surveyResults);
        try {
            List<RecommendProductDTO> recommendations = recommendProductService.getRecommendations(surveyResults);
            model.addAttribute("recommendations", recommendations);
            logger.info("Found {} recommended products", recommendations.size());
            return "recommendProductPage";
        } catch (Exception e) {
            logger.error("Error while processing recommendations", e);
            model.addAttribute("error", "An error occurred while processing your request.");
            return "errorPage";
        }
    }
}
