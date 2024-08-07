package com.multi.laptellect.error;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;

/**
 * 에러 핸들러 설정 클래스
 *
 * @author : 이강석
 * @fileName : GlobalExceptionHandler.java
 * @since : 2024-07-26
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    /**
     * 로그인 세션 없을 시 에러 처리 메서드
     *
     * @param ex       the ex
     * @param response the response
     * @throws IOException the io exception
     */
    @ExceptionHandler(IllegalStateException.class)
    public void handleIllegalStateException(IllegalStateException ex, HttpServletResponse response) throws IOException {
        response.sendRedirect("/signin");
    }
}

