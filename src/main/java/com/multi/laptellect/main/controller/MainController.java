package com.multi.laptellect.main.controller;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
public class MainController {

    @GetMapping("/")
    public String main(HttpSession httpSession){
        String sessionKey = "Session:" + "user";
        if (httpSession.getAttribute(sessionKey) == null) {
            httpSession.setAttribute("mySessionAttribute", "This is my session data");

            log.info("세션 없음 = {}", httpSession.getAttribute(sessionKey));
            // 세션에 조회 여부 기록
            httpSession.setAttribute(sessionKey, true);
        } else {
            log.info("세션 이미 있음 = {}", httpSession.getAttribute(sessionKey));
        }

        return "common/main";
    }

    @GetMapping("/hello")
    public String hellopage(){
        return "hello";
    }


}