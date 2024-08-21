package com.multi.laptellect.recommend.laptop.controller;

import com.multi.laptellect.product.model.dto.laptop.LaptopSpecDTO;
import com.multi.laptellect.product.service.ProductService;
import com.multi.laptellect.recommend.clovaapi.service.EmotionAnalyzeService;
import com.multi.laptellect.recommend.laptop.model.dto.CurationDTO;
import com.multi.laptellect.recommend.laptop.service.RecommendProductService;
import com.multi.laptellect.recommend.service.RedisCacheService;
import com.multi.laptellect.recommend.txttag.model.dto.TaggDTO;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
        log.info("사용자 선택지 값 = {}", curationDTO);
        try {
            ArrayList<LaptopSpecDTO> recommendations = recommendProductService.getRecommendations(curationDTO);
            String cacheKey = redisCacheService.saveCurationResult(curationDTO);

            session.setAttribute("lastCurationKey", cacheKey);

            addRecommendationsToModel(recommendations, model);
            model.addAttribute("curationDTO", curationDTO);
            return "recommend/recommendpage";
        } catch (Exception e) {
            log.error("에러 발생", e);
            model.addAttribute("error", "An error occurred while processing your request.");
            return "error";
        }
    }

    @GetMapping("/recommendpage")
    public String getLastRecommendations(Model model, HttpSession session) {
        String cacheKey = (String) session.getAttribute("lastCurationKey");
        if (cacheKey != null) {
            ArrayList<LaptopSpecDTO> recommendations = recommendProductService.getCachedCurationResult(cacheKey);
            if (recommendations != null) {
                addRecommendationsToModel(recommendations, model);
                return "recommend/recommendpage";
            }
        }
        return "redirect:/recommend";
    }

    private void addRecommendationsToModel(ArrayList<LaptopSpecDTO> recommendations, Model model) {
        Map<Integer, List<TaggDTO>> productTags = new HashMap<>();
        Map<Integer, String> sentiments = new HashMap<>();

        for (LaptopSpecDTO laptop : recommendations) {
            int productNo = laptop.getProductNo();
            List<TaggDTO> tags = recommendProductService.getTagsForProduct(productNo);
            productTags.put(productNo, tags);
            String sentiment = emotionAnalyzeService.analyzeSentiment(productNo);
            sentiments.put(productNo, sentiment);
        }

        model.addAttribute("recommendations", recommendations);
        model.addAttribute("productTags", productTags);
        model.addAttribute("sentiments", sentiments);
    }

}