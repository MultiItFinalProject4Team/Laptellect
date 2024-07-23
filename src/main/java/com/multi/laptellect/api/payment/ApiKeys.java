package com.multi.laptellect.api.payment;

import org.springframework.stereotype.Component;

@Component
public class ApiKeys {
    // 포트원 API 키
    private static final String IAMPORT_API_KEY = "6461077882447564";

    // 포트원 API 시크릿
    private static final String IAMPORT_API_SECRET = "hFvR3YmO9oG0qoeTEJVkOkrqlZD5NEbyOryiunBgPXurMe0tjowr3m1Jhf9AyPTUxVjeF4w3kjgDIL08";

    // API 키 getter 메소드
    public String getIamportApiKey() {
        return IAMPORT_API_KEY;
    }

    // API 시크릿 getter 메소드
    public String getIamportApiSecret() {
        return IAMPORT_API_SECRET;
    }
}