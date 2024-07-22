package com.multi.laptellect.recommend.clovaapi.service;//package com.multi.laptellect.recommend.clovaapi.service;

import com.google.gson.JsonObject;
import com.multi.laptellect.recommend.clovaapi.model.dao.SentimentDAO;
import com.multi.laptellect.recommend.clovaapi.model.dto.SentimentDTO;
import com.multi.laptellect.recommend.clovaapi.model.dto.SentimentProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;

@Slf4j
@Service
@RequiredArgsConstructor //이란 ? 생성자 주입을 위한 어노테이션 //생성자 주입 : 생성자를 통해 의존성 주입을 받는 방법 //생성자 주입을 사용하면 final 키워드를 사용할 수 있어서 불변성을 보장

public class EmotionAnalyzeService {

    private final SentimentProperties sentimentProperties; // SentimentProperties 클래스를 사용하기 위해 선언
    // SentimentProperties 클래스는 application.properties에 있는 sentiment.clientId와 sentiment.clientSecret 값을 가져오기 위해 사용
    // SentimentProperties 클래스는 @ConfigurationProperties(prefix = "sentiment")로 설정되어 있음
    // @ConfigurationProperties(prefix = "sentiment")는 application.properties에 있는 sentiment로 시작하는 값들을 가져옴
    //yml파일에서도 사용 가능




    private final SentimentDAO sentimentDAO;

    public HashMap<String, Object> getAnalyzeResult(String content, int productNo2) {
        String url = "https://naveropenapi.apigw.ntruss.com/sentiment-analysis/v1/analyze";
        HashMap<String, Object> result = new HashMap<>();

        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            //clientId와 clientSecret을 sentimentProperties에서 가져오도록 변경
            headers.add("X-NCP-APIGW-API-KEY-ID", sentimentProperties.getClientId());
            headers.add("X-NCP-APIGW-API-KEY", sentimentProperties.getClientSecret());

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("content", content);

            HttpEntity<String> entity = new HttpEntity<>(jsonObject.toString(), headers);
            ResponseEntity<HashMap> response = restTemplate.exchange(url, HttpMethod.POST, entity, HashMap.class);

            result.put("statusCode", response.getStatusCodeValue());
            result.put("header", response.getHeaders());
            result.put("body", response.getBody());

            // 분석 결과를 DB에 저장
            if (response.getBody() != null) {
                HashMap<String, Object> document = (HashMap<String, Object>) response.getBody().get("document");
                HashMap<String, Double> sentiment = (HashMap<String, Double>) document.get("sentiment");

                SentimentDTO sentimentDTO = new SentimentDTO();
                sentimentDTO.setProduct_no2(productNo2);
                sentimentDTO.setSentiment_positive(sentiment.get("positive"));
                sentimentDTO.setSentiment_negative(sentiment.get("negative"));
                sentimentDTO.setSentiment_neutral(sentiment.get("neutral"));

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

//전체 코드 흐름 : EmotionAnalyzeController에서 getAnalyzeResult 메소드를 호출하면 EmotionAnalyzeService에서 CLOVA Sentiment API를 호출하고 결과값을 반환함
//EmotionAnalyzeService에서는 RestTemplate을 사용하여 CLOVA Sentiment API를 호출하고 결과값을 반환함
//RestTemplate은 HTTP 통신을 위한 클래스로, API 호출을 위해 사용됨
//RestTemplate의 exchange 메소드를 사용하여 API 호출을 하고 결과값을 반환함
//exchange 메소드는 POST 방식으로 API를 호출하고 결과값을 반환함
//exchange 메소드의 인자로는 URL, HttpMethod, HttpEntity, 반환값의 타입이 들어감