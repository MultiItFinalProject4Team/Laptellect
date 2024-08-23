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
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmotionAnalyzeService {
    private final SentimentDAO sentimentDAO;

    @Value("${spring.sentiment.clientId}")
    private String clientId; //각각 API 키를 가져옴

    @Value("${spring.sentiment.clientSecret}")
    private String clientSecret;

    public HashMap<String, Object> getAnalyzeResult(int productNo, ReviewDTO reviewDTO) {

        String url = "https://naveropenapi.apigw.ntruss.com/sentiment-analysis/v1/analyze"; //API 주소
        HashMap<String, Object> result = new HashMap<>(); // 결과
        String review = reviewDTO.getContent(); //리뷰내용
        RestTemplate restTemplate = new RestTemplate();//RestTemplate 객체
        HttpHeaders headers = new HttpHeaders();//HttpHeaders 객체
        int positive;
        int negative;
        int neutral;

        try {
            log.info("api요청 준비");
            log.info("api호출");
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("X-NCP-APIGW-API-KEY-ID", clientId);
            headers.add("X-NCP-APIGW-API-KEY", clientSecret);

            JsonObject jsonObject = new JsonObject();//JSON 객체
            jsonObject.addProperty("content", review);

            HttpEntity<String> hent = new HttpEntity<>(jsonObject.toString(), headers);//HttpEntity 객체
            ResponseEntity<String> rent = restTemplate.exchange(url, HttpMethod.POST, hent, String.class);//API 호출
            log.info("api 응답 수신: {}", rent.getStatusCodeValue());

            result.put("statusCode", rent.getStatusCodeValue()); //응답함
            result.put("header", rent.getHeaders());//헤더

            //JSON 문자열을 Map으로 파싱 //gson사용
            Gson gson = new Gson();//Gson 객체
            Map<String, Object> body = gson.fromJson(rent.getBody(), Map.class);//body에저장
            result.put("body", body);
            log.info("감성 분석 결과: {}", body);//결과출력


            //분석 결과를 DB에 저장
            if (body != null && body.containsKey("document")) { //document키가 있따면?
                Map<String, Object> document = (Map<String, Object>) body.get("document"); //document키를 document에 저장
                Map<String, Double> confidence = (Map<String, Double>) document.get("confidence");//confidence키를 confidence에 저장

                SentimentDTO sentimentDTO = new SentimentDTO();//결과 넣을 dto
                sentimentDTO.setProductNo(productNo);
                //결과 저장
                positive = (int) confidence.get("positive").doubleValue();
                negative = (int) confidence.get("negative").doubleValue();
                neutral = (int) confidence.get("neutral").doubleValue();

                //가장 높은것에 카운트
                if (positive > negative && positive > neutral) {
                    sentimentDTO.setSentimentPositive(1);
                    sentimentDTO.setSentimentDenial(0);
                    sentimentDTO.setSentimentNeutrality(0);
                } else if (negative > positive && negative > neutral) {
                    sentimentDTO.setSentimentPositive(0);
                    sentimentDTO.setSentimentDenial(1);
                    sentimentDTO.setSentimentNeutrality(0);
                } else {
                    sentimentDTO.setSentimentPositive(0);
                    sentimentDTO.setSentimentDenial(0);
                    sentimentDTO.setSentimentNeutrality(1);
                }

                log.info("DB에 감성 분석 결과 저장 시도 - DTO: {}", sentimentDTO);
                try {
                    sentimentDAO.insertSentiment(sentimentDTO);
                    log.info("DB에 감성 분석 결과 저장 완료");
                } catch (DataAccessException e) {
                    log.error("DB 저장 중 오류 발생: {}", e.getMessage());
                }
            } else {
                log.warn("API 응답에서 document 키를 찾을 수 없습니다.");
            }

            return result;
        } catch (Exception e) {
            log.error("감성 분석 중 오류 발생: {}", e.getMessage());
        }
        return null;
    }



    public String analyzeSentiment(int productNo) {
        SentimentDTO sentiment = sentimentDAO.getSentimentByProductNo(productNo);
        log.info("상품 번호 {}: 감정 분석 결과 조회 - {}", productNo, sentiment);

        if (sentiment == null) {
            log.warn("상품 번호 {}: 감정 분석 결과 없음", productNo);
            return "분석 결과 없음";
        }

        int total = sentiment.getSentimentPositive() + sentiment.getSentimentDenial() + sentiment.getSentimentNeutrality();
        log.info("상품 번호 {}: 총 감정 분석 카운트 - {}", productNo, total);

        if (total == 0) {
            log.warn("상품 번호 {}: 총 감정 분석 카운트가 0입니다", productNo);
            return "분석 결과 없음";
        }

        int positivePercentage = (sentiment.getSentimentPositive() * 100) / total;
        int negativePercentage = (sentiment.getSentimentDenial() * 100) / total;
        log.info("상품 번호 {}: 긍정 비율 - {}%, 부정 비율 - {}%", productNo, positivePercentage, negativePercentage);

        String result;
        if (positivePercentage >= 90) {
            result = "압도적 긍정적";
        } else if (positivePercentage >= 70) {
            result = "매우 긍정적";
        } else if (positivePercentage >= 50) {
            result = "조금 긍정적";
        } else if (negativePercentage >= 90) {
            result = "압도적 부정적";
        } else if (negativePercentage >= 70) {
            result = "매우 부정적";
        } else if (negativePercentage >= 50) {
            result = "조금 부정적";
        } else {
            result = "중립적";
        }

        log.info("상품 번호 {}: 최종 감정 분석 결과 - {}", productNo, result);
        return result;
    }

    public void analyzeAllUnanlyzedReviews() {
        List<ReviewDTO> reviews = sentimentDAO.getUnanalyzedReviews();
        for (ReviewDTO review : reviews) {
            getAnalyzeResult(review.getProductNo(), review);
        }
    }
}