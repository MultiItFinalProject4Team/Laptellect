package com.multi.laptellect.recommend.laptop.controller;


import com.multi.laptellect.recommend.laptop.model.dto.LaptopDTO;
import com.multi.laptellect.recommend.laptop.model.dto.RecommendationRequestDTO;
import com.multi.laptellect.recommend.laptop.service.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/recommend")
public class RecommendationController {

    @Autowired
    private RecommendationService recommendationService;

    @PostMapping
    public ResponseEntity<List<LaptopDTO>> getRecommendations(@RequestBody RecommendationRequestDTO request) {
        List<LaptopDTO> recommendations = recommendationService.getRecommendations(request);
        return ResponseEntity.ok(recommendations);
    }
}