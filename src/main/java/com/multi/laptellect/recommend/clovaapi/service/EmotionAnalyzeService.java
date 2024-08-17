package com.multi.laptellect.recommend.clovaapi.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.multi.laptellect.product.model.dto.ReviewDTO;
import com.multi.laptellect.recommend.clovaapi.model.dao.SentimentDAO;
import com.multi.laptellect.recommend.clovaapi.model.dto.SentimentDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmotionAnalyzeService {
    private final SentimentDAO sentimentDAO;

    @Value("${spring.sentiment.clientId}")
    private String clientId;

    @Value("${spring.sentiment.clientSecret}")
    private String clientSecret;

    public HashMap<String, Object> getAnalyzeResult(int productNo, ReviewDTO reviewDTO) {
        log.info("감성 분석 시작 - 상품 번호: {}, 리뷰 내용: {}", productNo, reviewDTO.getContent());
        String url = "https://naveropenapi.apigw.ntruss.com/sentiment-analysis/v1/analyze";
        HashMap<String, Object> result = new HashMap<>(); // 결과
        String review = reviewDTO.getContent(); //리뷰내용
        RestTemplate restTemplate = new RestTemplate();//RestTemplate 객체
        HttpHeaders headers = new HttpHeaders();//HttpHeaders 객체

        try {
            log.info("api요청 준비");
            log.info("api호출");
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("X-NCP-APIGW-API-KEY-ID", clientId);
            headers.add("X-NCP-APIGW-API-KEY", clientSecret);

            JsonObject jsonObject = new JsonObject();//JSON 객체
            jsonObject.addProperty("content", review);

            HttpEntity<String> entity = new HttpEntity<>(jsonObject.toString(), headers);//HttpEntity 객체
            ResponseEntity<String> ent = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);//API 호출
            log.info("api 응답 수신: {}", ent.getStatusCodeValue());

            result.put("statusCode", ent.getStatusCodeValue()); //응답함
            result.put("header", ent.getHeaders());//헤더

            //JSON 문자열을 Map으로 파싱 //gson사용
            Gson gson = new Gson();//Gson 객체
            Map<String, Object> body = gson.fromJson(ent.getBody(), Map.class);//body에저장
            result.put("body", body);
            log.info("감성 분석 결과: {}", body);//결과출력

            //분석 결과를 DB에 저장
            if (body != null && body.containsKey("document")) { //document키가 있따면?
                Map<String, Object> document = (Map<String, Object>) body.get("document"); //document키를 document에 저장
                Map<String, Double> confidence = (Map<String, Double>) document.get("confidence");//confidence키를 confidence에 저장

                SentimentDTO sentimentDTO = new SentimentDTO();//결과 넣을 dto
                sentimentDTO.setProduct_no(productNo);
                //결과 저장
                sentimentDTO.setSentiment_positive(confidence != null ? confidence.getOrDefault("positive", 0.0) : 0.0);
                sentimentDTO.setSentiment_denial(confidence != null ? confidence.getOrDefault("denial", 0.0) : 0.0);
                sentimentDTO.setSentiment_neutrality(confidence != null ? confidence.getOrDefault("neutral", 0.0) : 0.0);
                log.info("DB에 감성 분석 결과 저장 시도 - DTO: {}", sentimentDTO);
                try {
                    sentimentDAO.insertSentiment(sentimentDTO);
                    log.info("db에 감성 분석 결과 저장");
                } catch (DataAccessException e) {
                    log.error("db 저장 중 오류남: {}", e.getMessage());
                    // 적절한 예외 처리 로직 추가
                }

                sentimentDAO.insertSentiment(sentimentDTO);
                log.info("감성 분석 결과 저장");
            } else {
                log.warn("값 확인");
            }

            return result;
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.error("HTTP 오류 발생: {}", e.getMessage());
            result.put("statusCode", e.getRawStatusCode());
            result.put("body", e.getStatusText());
            log.error("Error: " + e.toString());
            return result;
        } catch (Exception e) {
            log.error("예외 발생: {}", e.getMessage());
            result.put("statusCode", "999");
            result.put("body", "exception 발생");
            log.error("Error: " + e.toString());
            return result;
        }
    }
}