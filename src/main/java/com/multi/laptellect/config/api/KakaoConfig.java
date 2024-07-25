package com.multi.laptellect.config.api;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class KakaoConfig {
    @Value("${spring.kakao.key}")
    private String kakaoApiKey;

    @Value("${spring.kakao.rest-key}")
    private String KakaoRestApiKey;

    @Value("${spring.kakao.redirect}")
    private String kakaoRedirectUri;

    @Value("${spring.kakao.secret-key}")
    private String kakaoSecretKey;

}
