package com.multi.laptellect.auth.controller;

import com.multi.laptellect.auth.model.dto.TokenDTO;
import com.multi.laptellect.auth.service.AuthService;
import com.multi.laptellect.config.Security.JwtTokenProvider;
import com.multi.laptellect.member.model.dto.MemberDTO;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/signin")
    public String showLoginForm() {
        return "auth/auth-sign-in";
    }

    @PostMapping("/signin-post")
    public String login(MemberDTO memberDTO, HttpServletResponse response) {
        log.info("로그인 폼 전달 = {}", memberDTO);
        TokenDTO token = authService.login(memberDTO);

        // HttpOnly 쿠키
        Cookie accessTokenCookie = new Cookie("accessToken", token.getAccessToken());
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(true); // HTTPS에서만 쿠키가 전송되도록 설정
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge((int) jwtTokenProvider.getAccessExpirationTime() / 1000); // 쿠키의 유효 기간 (30분)

        Cookie refreshTokenCookie = new Cookie("refreshToken", token.getRefreshToken());
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true); // HTTPS에서만 쿠키가 전송되도록 설정
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge((int) jwtTokenProvider.getRefreshExpirationTime() / 1000); // 쿠키의 유효 기간 (1일)

        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);

        return "redirect:/";
    }

    @GetMapping("/signout")
    public void memberSignOut(){}

    @GetMapping("/signup")
    public String showSignUpForm(Model model) {
        MemberDTO memberDTO = new MemberDTO();
        model.addAttribute("member", memberDTO);

        return  "auth/auth-sign-up.html";
    }

    @PostMapping("/signup")
    public String createMember(MemberDTO memberDTO, RedirectAttributes redirectAttributes) {
        log.info("log info = {}", memberDTO);

        try {
            authService.createMember(memberDTO);
            redirectAttributes.addFlashAttribute("Message", "회원 가입 완료.");
        } catch (Exception e) {
            log.error("log error = {}", e);
            redirectAttributes.addFlashAttribute("Message", "회원 가입 중 오류가 발생했습니다.");
            return "redirect:/signup";
        }

        return "redirect:/";
    }
}
