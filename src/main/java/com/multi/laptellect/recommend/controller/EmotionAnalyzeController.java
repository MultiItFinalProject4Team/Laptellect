package src.main.java.com.multi.laptellect.recommend.controller;

import com.multi.springboot.clova.service.EmotionAnalyzeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequiredArgsConstructor
public class EmotionAnalyzeController {
    private final EmotionAnalyzeService emotionAnalyzeService;

    // Naver Cloud : CLOVA Sentiment API 테스트
    @GetMapping("/sentiment") // GET 방식으로 /sentiment URL 호출 시
    public HashMap<String, Object> getAnalyzeResult(@RequestBody String content) { // Request Body에 content를 받아옴
        return emotionAnalyzeService.getAnalyzeResult(content); // Service에서 처리한 결과값을 반환

    }
}