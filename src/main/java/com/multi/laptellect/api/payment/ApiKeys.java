package com.multi.laptellect.api.payment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ApiKeys {
    @Value("${spring.iamport.api-key}")
    private String iamportApiKey;

    @Value("${spring.iamport.api-secret}")
    private String iamportApiSecret;

    public String getIamportApiKey() {
        return iamportApiKey;
    }

    public String getIamportApiSecret() {
        return iamportApiSecret;
    }
}