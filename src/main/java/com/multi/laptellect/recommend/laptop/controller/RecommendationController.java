package com.multi.laptellect.recommend.laptop.controller;

import com.multi.laptellect.product.service.ProductService;
import com.multi.laptellect.recommend.clovaapi.service.EmotionAnalyzeService;
import com.multi.laptellect.recommend.laptop.model.dto.CurationDTO;
import com.multi.laptellect.recommend.laptop.service.RecommendProductService;
import com.multi.laptellect.recommend.service.RedisCacheService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Controller
public class RecommendationController {

    private final RecommendProductService recommendProductService;
    private final EmotionAnalyzeService emotionAnalyzeService;
    private final RedisCacheService redisCacheService;
    private final ProductService productService;

        @GetMapping("/recommend")
        public String showRecommendationForm(Model model, HttpSession session) {
            String cacheKey = (String) session.getAttribute("lastCurationKey");
            if (cacheKey != null) {
                try {
                    CurationDTO lastCuration = redisCacheService.getCurationResult(cacheKey);
                    if (lastCuration != null) {
                        model.addAttribute("curationDTO", lastCuration);
                    }
                } catch (Exception e) {
                    log.error("에러", e);
                }
            }
            return "recommend/recommend";
        }

        @PostMapping("/recommendpage")
        public String getRecommendations(CurationDTO curationDTO, Model model, HttpSession session) {
            Map<String, Object> modelMap = new HashMap<>();
            recommendProductService.processRecommendations(curationDTO, modelMap);

            if (modelMap.containsKey("error")) {
                model.addAttribute("error", modelMap.get("error"));
                return "error";
            }

            session.setAttribute("lastCurationKey", modelMap.get("cacheKey"));
            model.addAllAttributes(modelMap);
            return "recommend/recommendpage";
        }

        @GetMapping("/recommendpage")
        public String getLastRecommendations(Model model, HttpSession session) {
            String cacheKey = (String) session.getAttribute("lastCurationKey");
            Map<String, Object> modelMap = new HashMap<>();
            recommendProductService.getLastRecommendations(cacheKey, modelMap);

            if (modelMap.isEmpty()) {
                return "redirect:/recommend";
            }

            model.addAllAttributes(modelMap);
            return "recommend/recommendpage";
        }
    }