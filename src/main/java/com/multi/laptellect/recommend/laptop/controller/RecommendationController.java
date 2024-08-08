package com.multi.laptellect.recommend.laptop.controller;

import com.multi.laptellect.product.model.dto.laptop.LaptopSpecDTO;
import com.multi.laptellect.recommend.laptop.service.RecommendProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Map;
@Slf4j
@RequiredArgsConstructor
@Controller
public class RecommendationController {

    private static final Logger log = LoggerFactory.getLogger(RecommendationController.class);


    private final RecommendProductService recommendProductService;

    @GetMapping("/recommend")
    public String showRecommendationForm() {
        return "recommend/recommend";
    }

    @PostMapping("/recommendpage")
    public String getRecommendations(@RequestParam Map<String, String> surveyResults, Model model) {
        log.info("사용자 선택지 값 = ()", surveyResults);
        try {
            ArrayList<LaptopSpecDTO> recommendations = recommendProductService.getRecommendations(surveyResults);

            model.addAttribute("recommendations", recommendations);
            model.addAttribute("surveyResults", surveyResults);  // 추가: 설문 결과를 모델에 추가
            return "recommend/recommendpage";
        } catch (Exception e) {
            log.error("에러 발생", e);
            model.addAttribute("error", "An error occurred while processing your request.");
            return "error";
        }
    }
}