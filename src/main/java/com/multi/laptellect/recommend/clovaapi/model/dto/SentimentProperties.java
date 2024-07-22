package com.multi.laptellect.recommend.clovaapi.model.dto;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "sentiment")
public class SentimentProperties {

    private String clientId;
    private String clientSecret;
}
