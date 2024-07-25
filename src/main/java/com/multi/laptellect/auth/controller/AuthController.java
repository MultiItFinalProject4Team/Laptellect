package com.multi.laptellect.auth.controller;

import com.multi.laptellect.member.model.dto.KakaoDTO;
import com.multi.laptellect.auth.service.AuthService;
import com.multi.laptellect.auth.service.OAuthService;
import com.multi.laptellect.config.api.KakaoConfig;
import com.multi.laptellect.member.model.dto.MemberDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AuthController {
    private final KakaoConfig kakaoConfig;
    private final AuthService authService;
    private final OAuthService oAuthService;

    @GetMapping("/signin")
    public String showSignInForm(Model model) {

        return "auth/auth-sign-in";
    }

    @GetMapping("/signup")
    public String showSignUpForm(Model model) {
        MemberDTO memberDTO = new MemberDTO();
        model.addAttribute("member", memberDTO);

        return  "auth/auth-sign-up-form.html";
    }

    @GetMapping("/signin/kakao")
    public String kakaoSingIn() {
        String kakaoApiKey = kakaoConfig.getKakaoApiKey();
        String redirectURL = kakaoConfig.getKakaoRedirectUri();
        String loginURL = "https://kauth.kakao.com/oauth/authorize?response_type=code"
                + "&client_id=" + kakaoApiKey
                + "&redirect_uri=" + redirectURL;
        return "redirect:" + loginURL;
    }


    @RequestMapping("/signin/oauth/kakao")
    public String kakaoSignIn(@RequestParam("code") String code) {
        try {
            String token = oAuthService.getKakaoAccessToken(code);
            KakaoDTO kakaoDTO = oAuthService.getKaKaoProfileInfo(token);

            oAuthService.processKakaoUser(kakaoDTO);
        } catch (Exception e) {

        }

        return "redirect:/";
    }

}
