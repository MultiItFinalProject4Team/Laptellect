package com.multi.laptellect.main.controller;

import com.multi.laptellect.main.service.MainService;
import com.multi.laptellect.util.RedisUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
@RequiredArgsConstructor
public class MainController {
    private final RedisUtil redisUtil;
    private final MainService mainService;

    @GetMapping("/")
    public String main(HttpSession httpSession, HttpServletRequest httpServletRequest) {
        mainService.findLaptop();
        mainService.findMouse();
        mainService.findKeyboard();

        return "common/main";
    }
}