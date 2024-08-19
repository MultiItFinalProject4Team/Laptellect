package com.multi.laptellect.recommend.laptop.controller;

import com.multi.laptellect.product.model.dto.laptop.LaptopSpecDTO;
import com.multi.laptellect.recommend.clovaapi.service.EmotionAnalyzeService;
import com.multi.laptellect.recommend.laptop.model.dto.CurationDTO;
import com.multi.laptellect.recommend.laptop.service.RecommendProductService;
import com.multi.laptellect.recommend.txttag.model.dto.TaggDTO;
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

    @GetMapping("/recommend")
    public String showRecommendationForm() {
        return "recommend/recommend";
    }

    @PostMapping("/recommendpage")
    public String getRecommendations(CurationDTO curationDTO, Model model) {
        log.info("사용자 선택지 값 = {}", curationDTO);
        try {
            ArrayList<LaptopSpecDTO> recommendations = recommendProductService.getRecommendations(curationDTO);
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
            return "recommend/recommendpage";
        } catch (Exception e) {
            log.error("에러 발생", e);
            model.addAttribute("error", "An error occurred while processing your request.");
            return "error";
        }
    }
//    @PostMapping("/productList") //어떻게 해야할까
//    public String getProductList(Model model) {
//        try {
//            ArrayList<LaptopSpecDTO> products = recommendProductService.getAllProducts();
//            model.addAttribute("products", products);
//            return "recommend/productList";
//        } catch (Exception e) {
//            log.error("에러 발생", e);
//            model.addAttribute("에러", "에러 밸상.");
//            return "에러";
//        }
    }
