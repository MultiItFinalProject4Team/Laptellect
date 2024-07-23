package com.multi.laptellect.api.auth;

import com.multi.laptellect.auth.service.AuthService;
import com.multi.laptellect.common.model.Email;
import com.multi.laptellect.member.model.dto.CustomUserDetails;
import com.multi.laptellect.member.model.dto.MemberDTO;
import com.multi.laptellect.member.service.MemberService;
import com.multi.laptellect.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuthApiController {
    private final AuthService authService;
    private final MemberService memberService;

    @ResponseBody
    @PostMapping("/check-id")
    public boolean isId(@RequestParam("userName") String id) {
        return authService.isMemberById(id);
    }

    @ResponseBody
    @PostMapping("/check-nickname")
    public boolean isNickName(@RequestParam("nickName") String nickName) {
        return authService.isMemberByNickName(nickName);
    }

    @ResponseBody
    @PostMapping("/check-email")
    public boolean isEmail(@RequestParam("email") String email) {
        return authService.isMemberByEmail(email);
    }

    @ResponseBody
    @PostMapping("/check-password")
    public boolean isPassword(@RequestParam("password") String password) {
        return authService.isMemberByPassword(password);
    }

    @ResponseBody
    @PostMapping("/verify-email")
    public boolean sendVerifyEmail(@RequestParam("email") String userEmail) {
        Email email = new Email();
        email.setReceiveAddress(userEmail);

        try {
            authService.sendVerifyEmail(email);
            return true;
        } catch (Exception e) {
            log.error("sendEmail Error = ", e);
            return false;
        }
    }

    @ResponseBody
    @PostMapping("/check-verify-email")
    public boolean isVerifyEmail(@RequestParam("verifyCode") String verifyCode) {
        try {
            if(authService.isVerifyEmail(verifyCode)) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            log.error("Verify Code Error = ", e);
            return false;
        }
    }

    @ResponseBody
    @PostMapping("/update-email")
    public boolean updateEmail(@RequestParam("email") String userEmail, @RequestParam("verifyCode") String verifyCode) {
        CustomUserDetails userDetails = SecurityUtil.getUserDetails();
        MemberDTO memberDTO = new MemberDTO();

        memberDTO.setMemberNo(userDetails.getMemberNo());
        memberDTO.setEmail(userEmail);

        try {
            if(memberService.updateEmail(memberDTO, verifyCode)) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            log.error("Email Update Code Error = ", e);
            return false;
        }
    }

    @ResponseBody
    @PostMapping("/update-nickname")
    public boolean updateNickName(@RequestParam("nickName") String nickName) {
        CustomUserDetails userDetails = SecurityUtil.getUserDetails();
        MemberDTO memberDTO = new MemberDTO();

        memberDTO.setMemberNo(userDetails.getMemberNo());
        memberDTO.setNickName(nickName);

        try {
            if(memberService.updateNickName(memberDTO)) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            log.error("NickName Update Code Error = ", e);
            return false;
        }
    }

    @ResponseBody
    @PostMapping("/update-password")
    public boolean updatePassword(@RequestParam("password") String passsword) {
        CustomUserDetails userDetails = SecurityUtil.getUserDetails();
        MemberDTO memberDTO = new MemberDTO();

        memberDTO.setMemberNo(userDetails.getMemberNo());
        memberDTO.setPassword(passsword);

        try {
            if(memberService.updateNickName(memberDTO)) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            log.error("NickName Update Code Error = ", e);
            return false;
        }
    }

    @ResponseBody
    @PostMapping("/verify-tel")
    public boolean sendVerifySms(@RequestParam("tel") String tel) {
        try {
            authService.sendSms(tel);
            return true;
        } catch (Exception e) {
            log.error("sendSms Error = ", e);
            return false;
        }
    }

    @ResponseBody
    @PostMapping("/check-verify-tel")
    public boolean isVerifyTel(@RequestParam("verifyCode") String verifyCode) {
        try {
            if(authService.isVerifyTel(verifyCode)) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            log.error("Verify Code Error = ", e);
            return false;
        }
    }

    @ResponseBody
    @PostMapping("/update-tel")
    public boolean updateTel(@RequestParam("tel") String tel, @RequestParam("verifyCode") String verifyCode) {
        CustomUserDetails userDetails = SecurityUtil.getUserDetails();
        MemberDTO memberDTO = new MemberDTO();

        memberDTO.setMemberNo(userDetails.getMemberNo());
        memberDTO.setTel(tel);

        try {
            if(memberService.updateTel(memberDTO, verifyCode)) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            log.error("Tel Update Code Error = ", e);
            return false;
        }
    }
}
