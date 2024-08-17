package com.multi.laptellect.config.Security;

import com.multi.laptellect.common.service.LogService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Please explain the class!!
 *
 * @author : 이강석
 * @fileName : CustomAuthenticationSuccessHandler
 * @since : 2024-08-17
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final LogService logService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String userName = authentication.getName();

        try {
            logService.saveLoginLog(request, userName);
        } catch (Exception e) {
            log.error("로그인 로그 DB 삽입 실패");
        }

        log.info("로그인 성공 = {}", userName);
        response.sendRedirect("/");
    }
}
