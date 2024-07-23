package com.multi.laptellect.auth.controller;

import com.multi.laptellect.auth.service.AuthService;
import com.multi.laptellect.member.model.dto.MemberDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
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

    @PostMapping("/signin")
    public String login(MemberDTO memberDTO, HttpServletResponse response) {
        log.info("로그인 폼 전달 = {}", memberDTO);

        return "redirect:/";
    }

    @GetMapping("/signout")
    public String logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
        return "redirect:/";
    }

    @GetMapping("/signup")
    public String showSignUpForm(Model model) {
        MemberDTO memberDTO = new MemberDTO();
        model.addAttribute("member", memberDTO);

        return  "auth/auth-sign-up-form.html";
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

    @ResponseBody
    @PostMapping("/api/check-id")
    public boolean isId(@RequestParam("userName") String id) {
        System.out.println("테스트");
        return authService.isMemberById(id);
    }

    @ResponseBody
    @PostMapping("/api/check-email")
    public boolean isEmail(@RequestParam("email") String email) {
        System.out.println("테스트");
        return authService.isMemberByEmail(email);
    }

//    @ResponseBody
//    @PostMapping("/check-nickname")
//    public boolean isPassword(@RequestParam("nickName") String nickName) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
//
//        return authService.isMemberByNickName(password, userDetails);
//    }
}
