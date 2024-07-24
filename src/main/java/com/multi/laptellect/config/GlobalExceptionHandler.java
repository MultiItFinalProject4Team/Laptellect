package com.multi.laptellect.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(IllegalStateException.class)
    public void handleIllegalStateException(IllegalStateException ex, HttpServletResponse response) throws IOException {
        response.sendRedirect("/signin");
    }


}
