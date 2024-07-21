package com.multi.laptellect.auth.controller;

import com.multi.laptellect.auth.model.dto.TokenDTO;
import com.multi.laptellect.auth.service.AuthService;
import com.multi.laptellect.member.model.dto.MemberDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @GetMapping("/signin")
    public String showLoginForm() {
        return "auth/auth-sign-in";
    }

    @PostMapping("/signin-post")
    public TokenDTO login(@RequestBody MemberDTO memberDTO) {
        TokenDTO test = authService.login(memberDTO);

        return test;
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
