package com.multi.laptellect.recommend.clovaapi.controller;


import com.multi.laptellect.recommend.clovaapi.service.EmotionAnalyzeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequiredArgsConstructor
public class EmotionAnalyzeController {
    private final EmotionAnalyzeService emotionAnalyzeService;

    @PostMapping("/sentiment")
    public HashMap<String, Object> getAnalyzeResult(@RequestParam String content, @RequestParam int productNo2) {
        return emotionAnalyzeService.getAnalyzeResult(content, productNo2);
    }
}