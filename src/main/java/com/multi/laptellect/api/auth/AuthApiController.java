package com.multi.laptellect.api.auth;

import com.multi.laptellect.auth.service.AuthService;
import com.multi.laptellect.common.model.Email;
import com.multi.laptellect.member.model.dto.CustomUserDetails;
import com.multi.laptellect.member.model.dto.MemberDTO;
import com.multi.laptellect.member.service.MemberService;
import com.multi.laptellect.util.PasswordValidator;
import com.multi.laptellect.util.SecurityUtil;
import com.multi.laptellect.util.StringValidUtil;
import com.multi.laptellect.util.UserValidator;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * 인증/인가 관련 API 매핑에 사용하는 클래스
 *
 * @author : 이강석
 * @fileName : AuthApiController.java
 * @since : 2024-07-26
 */
@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuthApiController {
    private final AuthService authService;
    private final MemberService memberService;

    public enum SignupResult {
        SUCCESS,
        INVALID_USER_ID,
        INVALID_PASSWORD,
        USER_ID_ALREADY_EXISTS,
        EMAIL_ALREADY_EXISTS;
    }

    /**
     * 입력 받은 ID로 Member 유무 체크
     *
     * @param id 조회에 사용할 파라미터 값
     * @return the boolean
     */
    @ResponseBody
    @PostMapping("/check-id")
    public boolean isId(@RequestParam("userName") String id) {
        return authService.isMemberById(id);
    }

    /**
     * 입력 받은 닉네임으로 Member 유무 체크
     *
     * @param nickName 조회에 사용할 파라미터 값
     * @return the boolean
     */
    @ResponseBody
    @PostMapping("/check-nickname")
    public boolean isNickName(@RequestParam("nickName") String nickName) {
        return authService.isMemberByNickName(nickName);
    }

    /**
     * 입력 받은 email으로 Member 유무 체크
     *
     * @param email 조회에 사용할 파라미터 값
     * @return the boolean
     */
    @ResponseBody
    @PostMapping("/check-email")
    public boolean isEmail(@RequestParam("email") String email) {
        return authService.isMemberByEmail(email);
    }

    /**
     * Is 입력 받은 Password로 현재 Password와 일치한지 체크
     *
     * @param password 조회에 사용할 파라미터 값
     * @return the boolean
     */
    @ResponseBody
    @PostMapping("/check-password")
    public boolean isPassword(@RequestParam("beforePassword") String password) {
        return authService.isMemberByPassword(password);
    }

    /**
     * 변경 전 비밀번호와 변경 후 비밀번호 동일한지 검증
     *
     * @param beforePassword 변경 전 비밀번호
     * @param afterPassword  변경 후 비밀번호
     * @return the boolean
     */
    @ResponseBody
    @PostMapping("/check-after-password")
    public boolean isPasswordsDifferent(@RequestParam("beforePassword") String beforePassword,
                                        @RequestParam("afterPassword") String afterPassword) {
        return authService.isPasswordsDifferent(beforePassword, afterPassword);
    }

    /**
     * 이메일 인증 시 이메일을 보내는 메서드
     *
     * @param userEmail the user email
     * @return the boolean
     */
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

    /**
     * 이메일 인증 시 인증 코드가 맞는지 검증하는 메서드
     *
     * @param verifyCode the verify code
     * @return the boolean
     */
    @ResponseBody
    @PostMapping("/check-verify-email")
    public boolean isVerifyEmail(@RequestParam("verifyCode") String verifyCode, @RequestParam("email") String email) {
        try {
            if(authService.isVerifyEmail(email, verifyCode)) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            log.error("Verify Code Error = ", e);
            return false;
        }
    }

    /**
     * 사용자 이메일을 업데이트 메서드
     *
     * @param userEmail  the user email
     * @param verifyCode the verify code
     * @return the boolean
     */
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

    /**
     * 사용자 닉네임을 업데이트 하는 메서드
     *
     * @param nickName the nick name
     * @return the boolean
     */
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


    /**
     * 사용자 패스워드를 업데이트 하는 메서드
     *
     * @param beforePassword 사용자 변경 전 비밀번호
     * @param afterPassword  사용자 변경 후 비밀번호
     * @return the boolean
     */
    @ResponseBody
    @PostMapping("/update-password")
    public boolean updatePassword(@RequestParam("beforePassword") String beforePassword,
                                  @RequestParam("afterPassword") String afterPassword) {
        try {
            if(!PasswordValidator.validatePassword(afterPassword)) {
                log.warn("비밀번호 유효성 검증 실패 = {}", afterPassword);
                throw new RuntimeException("비밀번호 유효성 검증 실패 = " + afterPassword);
            }

            return memberService.updatePassword(beforePassword, afterPassword);
        } catch (Exception e) {
            log.error("Password Update Code Error = ", e);
            return false;
        }
    }


    /**
     * SMS 인증 시 문자를 보내는 메서드
     *
     * @param tel the tel
     * @return the boolean
     */
    @ResponseBody
    @PostMapping("/verify-tel")
    public boolean sendVerifySms(@RequestParam("tel") String tel) {
        tel = tel.replaceAll("[-\\s]", "");
        try {
            authService.sendSms(tel);
            return true;
        } catch (Exception e) {
            log.error("sendSms Error = ", e);
            return false;
        }
    }

    /**
     * SMS 인증번호를 검증하는 메서드
     *
     * @param verifyCode the verify code
     * @return the boolean
     */
    @ResponseBody
    @PostMapping("/check-verify-tel")
    public boolean isVerifyTel(@RequestParam("verifyCode") String verifyCode, @RequestParam("tel") String tel) {
        try {
            if(authService.isVerifyTel(verifyCode, tel)) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            log.error("Verify Code Error = ", e);
            return false;
        }
    }

    /**
     * 이메일 또는 전화번호를 통해 ID를 찾는 메서드
     *
     * @param email the email
     * @param tel   the tel
     * @return the string
     */
    @ResponseBody
    @PostMapping("/find-user-id")
    public boolean findUserId(@RequestParam(name = "email", required = false) String email,
                             @RequestParam(name = "tel", required = false) String tel) {
        boolean result;
        MemberDTO memberDTO = new MemberDTO();

        try {
            memberDTO.setEmail(email);
            memberDTO.setTel(tel);
            result = memberService.findUserId(memberDTO);
            return result;
        } catch (Exception e) {
            log.error("Find ID Error = ", e);
            return false;
        }
    }

    @ResponseBody
    @PostMapping("/send-temp-password")
    public int sendTempPassword(@RequestParam(name = "userId", required = false) String userId,
                                @RequestParam(name = "email", required = false) String email) {
        int result = 0;

        try {
            result = memberService.sendTempPassword(userId, email);

            log.info("Temp Email Send success", result);
            return result;
        } catch (Exception e) {
            log.error("sendTempPassword Error = ", e);
            result = 0;
            return result;
        }
    }


    /**
     * 회원가입을 실행하는 메서드
     *
     * @param memberDTO the member dto
     * @return the int
     */
    @ResponseBody
    @PostMapping("/signup")
    public int createMember(MemberDTO memberDTO, @RequestParam(name = "verifyCode") String verifyCode) {
        log.info("회원가입 실행 = {}", memberDTO);

        try {
            String userId = memberDTO.getMemberName();
            String userEmail = memberDTO.getEmail();
            String password = memberDTO.getPassword();

            if(!UserValidator.validateUserId(userId).equals("success")) {
                log.warn("아이디 유효성 검증 실패 = {}", userId);
                throw new RuntimeException("아이디 유효성 검증 실패 = " + userId);
            }

            if(!PasswordValidator.validatePassword(password)) {
                log.warn("비밀번호 유효성 검증 실패 = {}", password);
                throw new RuntimeException("비밀번호 유효성 검증 실패 = " + password);
            }

            if (authService.isMemberById(userId)) {
                log.warn("아이디 중복 = {}", userId);
                return 1; // ID 중복
            }
            if (authService.isMemberByEmail(userEmail)) {
                log.warn("이메일 중복 = {}", userEmail);
                return 2; // 이메일 중복
            }

            if (!authService.isVerifyEmail(userEmail, verifyCode)) {
                log.warn("이메일 불일치 = {}", userEmail);
                return 3;
            }

            authService.createMember(memberDTO);
            log.info("회원가입 완료 = {}", memberDTO);
            return 0; // 회원 가입 완료
        } catch (Exception e) {
            log.error("회원가입 에러 = " + e);
            return 4; // 회원가입 에러
        }
    }

    /**
     * 사용자 연락처를 업데이트 하는 메서드
     *
     * @param tel        the tel
     * @param verifyCode the verify code
     * @return the boolean
     */
    @ResponseBody
    @PostMapping("/update-tel")
    public boolean updateTel(@RequestParam("tel") String tel, @RequestParam("verifyCode") String verifyCode) {
        CustomUserDetails userDetails = SecurityUtil.getUserDetails();
        MemberDTO memberDTO = new MemberDTO();

        log.info("파라 확인 tel = {}, verifyCode = {}", tel, verifyCode);

        memberDTO.setMemberNo(userDetails.getMemberNo());
        memberDTO.setTel(tel.replaceAll("[-\\s]", ""));

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

    @ResponseBody
    @GetMapping("/delete-member")
    public boolean deleteId(HttpSession httpSession, HttpServletRequest request, HttpServletResponse response) {
        boolean result;
        try {
            result = memberService.deleteMember();
            httpSession.invalidate();
            Cookie cookie = new Cookie("remember-me", null);
            cookie.setPath("/");
            cookie.setMaxAge(0);
            response.addCookie(cookie);
            SecurityContextHolder.clearContext();
        } catch (Exception e) {
            log.error("회원 탈퇴 실패 = ", e);
            result = false;
        }
        return result;
    }

    @ResponseBody
    @PostMapping("/check-registration-no")
    public int isRegistrationNo(MemberDTO memberDTO) {
        int result = 0;

        log.info("파라미터 체크 = {}", memberDTO);

        boolean isOwnerName = StringValidUtil.isValidString(memberDTO.getOwnerName());
        boolean isBusinessDate = StringValidUtil.isValidString(memberDTO.getBusinessDate());
        boolean isRegistrationNo = StringValidUtil.isValidString(memberDTO.getRegistrationNo());

        if(!isOwnerName || !isBusinessDate || !isRegistrationNo)  {
            return 3;
        }

        try {
            return authService.isRegistrationNo(memberDTO);
        } catch (Exception e) {
            log.error("사업자 등록번호 조회 에러 = ", e);
            return result;
        }
    }


}
