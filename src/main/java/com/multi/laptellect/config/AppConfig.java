package com.multi.laptellect.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Please explain the class!!
 *
 * @author : 이강석
 * @fileName : AppConfig
 * @since : 2024-08-13
 */
@Configuration
public class AppConfig {
    @Bean
    public AtomicInteger atomicInteger() {
        return new AtomicInteger();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
