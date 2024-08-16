package com.multi.laptellect.recommend.clovaapi.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.multi.laptellect.product.model.dto.ReviewDTO;
import com.multi.laptellect.recommend.clovaapi.model.dao.SentimentDAO;
import com.multi.laptellect.recommend.clovaapi.model.dto.SentimentDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
        String url = "https://naveropenapi.apigw.ntruss.com/sentiment-analysis/v1/analyze";
        HashMap<String, Object> result = new HashMap<>();
        String review = reviewDTO.getContent();

        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("X-NCP-APIGW-API-KEY-ID", clientId);
            headers.add("X-NCP-APIGW-API-KEY", clientSecret);

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("content", review);

            HttpEntity<String> entity = new HttpEntity<>(jsonObject.toString(), headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);//, Map.class);

            result.put("statusCode", response.getStatusCodeValue());
            result.put("header", response.getHeaders());

            // JSON 문자열을 Map으로 파싱
            Gson gson = new Gson();
            Map<String, Object> body = gson.fromJson(response.getBody(), Map.class);
            result.put("body", body);

            // 분석 결과를 DB에 저장
            if (body != null && body.containsKey("document")) {
                Map<String, Object> document = (Map<String, Object>) body.get("document");
                Map<String, Double> confidence = (Map<String, Double>) document.get("confidence");

                SentimentDTO sentimentDTO = new SentimentDTO();
                sentimentDTO.setProduct_no(productNo);
                sentimentDTO.setSentiment_positive(confidence.get("positive"));
                sentimentDTO.setSentiment_denial(confidence.get("denial"));
                sentimentDTO.setSentiment_neutrality(confidence.get("neutral"));

                sentimentDAO.insertSentiment(sentimentDTO);
            }

            return result;
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            result.put("statusCode", e.getRawStatusCode());
            result.put("body", e.getStatusText());
            log.error("Error: " + e.toString());
            return result;
        } catch (Exception e) {
            result.put("statusCode", "999");
            result.put("body", "exception 발생");
            log.error("Error: " + e.toString());
            return result;
        }
    }
}