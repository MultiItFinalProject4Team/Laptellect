package com.multi.laptellect.main.controller;

import com.multi.laptellect.util.RedisUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.format.DateTimeFormatter;

@Controller
@Slf4j
@RequiredArgsConstructor
public class MainController {
    private final RedisUtil redisUtil;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @GetMapping("/")
    public String main(HttpSession httpSession, HttpServletRequest httpServletRequest) {
        return "common/main";
    }

    @GetMapping("/hello")
    public String hellopage(){
        return "hello";
    }


}