package com.multi.laptellect.config.api;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Email 설정 클래스
 *
 * @fileName      : EmailConfig.java
 * @author        : 이강석
 * @since         : 2024-07-26
 */
@Configuration
@Getter
public class EmailConfig {
    @Value("${spring.mail.host}")
    private String host;

    @Value("${spring.mail.port}")
    private int port;

    @Value("${spring.mail.username}")
    private String username;

    @Value("${spring.mail.password}")
    private String password;
}