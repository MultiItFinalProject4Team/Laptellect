package com.multi.laptellect.config.api;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * 카카오 API 설정 클래스
 *
 * @fileName      : KakaoConfig.java
 * @author        : 이강석
 * @since         : 2024-07-26
 */
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
