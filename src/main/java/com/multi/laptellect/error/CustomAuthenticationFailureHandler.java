package com.multi.laptellect.error;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 인증 인가 에러 핸들러
 *
 * @author : 이강석
 * @fileName : CustomAuthenticationFailureHandler
 * @since : 2024-08-10
 */
@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        String errorMessage = "false";

        if (exception instanceof BadCredentialsException) {
            errorMessage = "password";
        } else if (exception instanceof UsernameNotFoundException) {
            errorMessage = "username";
        } else if (exception instanceof MemberNotFoundException) {
            errorMessage = "memberNotFound";
        }

        response.sendRedirect("/signin?error=" + errorMessage);
    }
}
