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

//@RequiredArgsConstructor : final이나 @NonNull인 필드 값만 파라미터로 받는 생성자를 만들어준다.
// @RequestParam : 요청 파라미터를 메소드 파라미터에 매핑하는 어노테이션
// @PostMapping : POST 요청을 처리하는 메소드에 사용하는 어노테이션
// @RestController : REST API를 처리하는 컨트롤러에 사용하는 어노테이션
// HashMap : 키와 값으로 구성된 데이터를 저장하는 자료구조
// getAnalyzeResult : content와 productNo2를 받아서 감정 분석 결과를 반환하는 메소드
// content : 감정 분석 대상 텍스트
// productNo2 : 감정 분석 대상 제품 번호
// emotionAnalyzeService.getAnalyzeResult(content, productNo2) : 감정 분석 서비스를 호출하여 감정 분석 결과를 반환
// return : 감정 분석 결과를 반환