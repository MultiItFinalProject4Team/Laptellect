package com.multi.laptellect.util;

import com.multi.laptellect.member.model.dto.CustomUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Slf4j
public class SecurityUtil {
    public static CustomUserDetails getUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            log.error("log error = {}", "로그인 하지 않은 사용자 입니다.");
            throw new IllegalStateException("로그인 하지 않은 사용자 입니다.");
        }

        return (CustomUserDetails) authentication.getPrincipal();
    }
}
