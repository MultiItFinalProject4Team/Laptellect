package com.multi.laptellect.auth.controller;

import com.multi.laptellect.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @GetMapping("/signin")
    public String showLoginForm() {
        return "auth/auth-sign-in";
    }

    @GetMapping("/signout")
    public void memberSignOut(){}

    @GetMapping("/signup")
    public String showSignUpForm() {
        String path = "auth/auth-sign-up.html";

        return path;
    }
}
