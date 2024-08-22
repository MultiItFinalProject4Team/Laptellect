package com.multi.laptellect.error;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Please explain the class!!
 *
 * @author : 이강석
 * @fileName : accessDeniedHandler
 * @since : 2024-08-21
 */
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        String errorMessage = "AccessDenied";

        response.sendRedirect("/signin?error=" + errorMessage);
    }
}
