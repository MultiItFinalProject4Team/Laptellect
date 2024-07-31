package com.multi.laptellect.config.api;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * 구글 로그인 API 설정 클래스
 *
 * @author : 이강석
 * @fileName : GoogleConfig
 * @since : 2024-07-30
 */
@Configuration
@Getter
public class GoogleConfig {
    @Value("${spring.google.key}")
    private String googleApiKey;

    @Value("${spring.google.redirect}")
    private String googleRedirectUri;

    @Value("${spring.google.client-id}")
    private String googleClientId;

    @Value("${spring.google.client-secret}")
    private String googleClientSecretKey;


}
