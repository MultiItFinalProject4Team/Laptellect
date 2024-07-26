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

/**
 * 인증/인가에 사용되는 컨트롤러
 *
 * @author : 이강석
 * @fileName : AuthController.java
 * @since : 2024-07-26
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class AuthController {
    private final KakaoConfig kakaoConfig;
    private final AuthService authService;
    private final OAuthService oAuthService;

    /**
     * 로그인화면 출력 메서드
     *
     * @param model the model
     * @return the string
     */
    @GetMapping("/signin")
    public String showSignInForm(Model model) {

        return "auth/auth-sign-in";
    }

    /**
     * 회원가입 화면 출력 메서드
     *
     * @param model the model
     * @return the string
     */
    @GetMapping("/signup")
    public String showSignUpForm(Model model) {
        MemberDTO memberDTO = new MemberDTO();
        model.addAttribute("member", memberDTO);

        return  "auth/auth-sign-up-form.html";
    }

    /**
     * 카카오 로그인 및 회원가입 화면을 출력하는 메서드
     *
     * @return the string
     */
    @GetMapping("/signin/kakao")
    public String kakaoSingIn() {
        String kakaoApiKey = kakaoConfig.getKakaoApiKey();
        String redirectURL = kakaoConfig.getKakaoRedirectUri();
        String loginURL = "https://kauth.kakao.com/oauth/authorize?response_type=code"
                + "&client_id=" + kakaoApiKey
                + "&redirect_uri=" + redirectURL;
        return "redirect:" + loginURL;
    }


    /**
     * 카카오 로그인 CallBack 메서드
     *
     * @param code the code
     * @return the string
     */
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
