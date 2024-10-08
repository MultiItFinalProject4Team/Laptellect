package com.multi.laptellect.auth.controller;

import com.multi.laptellect.auth.service.AuthService;
import com.multi.laptellect.auth.service.OAuthService;
import com.multi.laptellect.config.api.GoogleConfig;
import com.multi.laptellect.config.api.KakaoConfig;
import com.multi.laptellect.member.model.dto.MemberDTO;
import com.multi.laptellect.member.model.dto.SocialDTO;
import com.multi.laptellect.util.SecurityUtil;
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
    private final GoogleConfig googleConfig;
    private final AuthService authService;
    private final OAuthService oAuthService;

    /**
     * 로그인화면 출력 메서드
     *
     * @param model the model
     * @return the string
     */
    @GetMapping("/signin")
    public String showSignInForm(@RequestParam(name = "error", required = false) String error, Model model) {
        if(error != null) {
            String errorMessage = "아이디 또는 비밀번호가 올바르지 않습니다.";

            if (error.equals("password")) {
                errorMessage = "비밀번호가 올바르지 않습니다.";
            } else if (error.equals("username")) {
                errorMessage = "존재하지 않는 사용자 입니다.";
            } else if (error.equals("memberNotFound")) {
                errorMessage = "탈퇴한 사용자 입니다.";
            } else if (error.equals("AccessDenied")) {
                errorMessage = "권한이 없는 사용자 입니다";
            }
            model.addAttribute("errorMessage", errorMessage);
        }

        return "auth/auth-sign-in";
    }

    @GetMapping("/signup")
    /**
     * 회원가입 화면 출력 메서드
     *
     * @param model the model
     * @return the string
     */
    public String showSignUpForm(Model model) {
        MemberDTO memberDTO = new MemberDTO();
        model.addAttribute("member", memberDTO);

        return  "auth/auth-sign-up-form";
    }

    @GetMapping("/signup/seller")
    public String showSellerSignUpForm() {
        return "auth/auth-seller-sign-up-form";
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
    public String kakaoSignIn(@RequestParam("code") String code, Model model) {
        try {
            String token = oAuthService.getKakaoAccessToken(code);
            SocialDTO socialDTO = oAuthService.getKaKaoProfileInfo(token);
            String message = oAuthService.processKakaoUser(socialDTO);

            model.addAttribute("message", message);
        } catch (Exception e) {
            log.error("카카오 로그인 에러 = ", e);
            model.addAttribute("message", "fail");
        }
        return "auth/auth-sign-in-success";
    }

    /**
     * 구글 로그인 출력 메서드
     *
     * @return Login URL
     */
    @GetMapping("/signin/google")
    public String googleSingIn() {
        String googleApiKey = googleConfig.getGoogleApiKey();
        String googleClientId = googleConfig.getGoogleClientId();
        String redirectURL = googleConfig.getGoogleRedirectUri();
        String loginURL = "https://accounts.google.com/o/oauth2/auth?client_id="
                + googleClientId
                + "&redirect_uri="
                + redirectURL
                + "&response_type=code"
                + "&scope=profile%20email";
        return "redirect:" + loginURL;
    }

    /**
     * 구글 로그인 callback 메서드
     *
     * @param code 구글 로그인 반환 code 값
     * @return the string
     */
    @GetMapping("/signin/oauth/google")
    public String googleSignIn(@RequestParam("code") String code, Model model) {
        log.debug("구글 리턴 code = {}", code);
        try {
            String token = oAuthService.getGoogleAccessToken(code);
            SocialDTO SocialDTO = oAuthService.getGoogleProfileInfo(token);

            String message = oAuthService.processGoogleUser(SocialDTO);
            model.addAttribute("message", message);
            log.info("메시지 리턴 확인 {}", message);
        } catch (Exception e) {
            log.error("Google Login Error = ", e);
            model.addAttribute("message", "fail");
        }
        
        return "auth/auth-sign-in-success";
    }

    @GetMapping("/find-id")
    public String findId() {
        return "auth/auth-find-id";
    }

    @GetMapping("/find-pw")
    public String findPw() {
        return "auth/auth-find-pw";
    }

    @GetMapping("/delete-id")
    public String deleteId() {
        String loginType = SecurityUtil.getUserDetails().getLoginType();

        try {
            switch (loginType) {
                case "local":
                    log.info("일반 회원 탈퇴 진행");
                    break;
                case "kakao":
                    log.info("카카오 회원 탈퇴 진행");
                    break;
                case "google":
                    log.info("구글 회원 탈퇴 진행");
                    break;
            }

            return "redirect:/";
        } catch (Exception e) {
            log.error("회원탈퇴 에러 = ", e);
            return "redirect:/";
        }
    }
}
