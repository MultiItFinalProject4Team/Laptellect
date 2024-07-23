package com.multi.laptellect.member.controller;

import com.multi.laptellect.member.model.dto.CustomUserDetails;
import com.multi.laptellect.member.service.MemberService;
import com.multi.laptellect.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@Slf4j
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

//    @GetMapping // 대시보드 시간 남으면 개발
//    public String showProfileDashboard(Model model) {
//        return "member/dashboard-profile";
//    }

    @GetMapping(value = {"/profile", "/profile/purchase"})
    public String showProfilePurchase(Model model) {
        CustomUserDetails userInfo = SecurityUtil.getUserDetails();

        log.debug("purchase find Start = {}", userInfo.getMemberNo());

        try {

        } catch (Exception e) {

        }

        model.addAttribute(userInfo);

        return "member/purchase-profile";
    }

    @GetMapping("/profile/review")
    public String showProfileReview(Model model) {
        CustomUserDetails userInfo = SecurityUtil.getUserDetails();

        log.debug("profile review find Start = {}", userInfo.getMemberNo());
        try {

        } catch (Exception e) {

        }

        model.addAttribute(userInfo);

        return "member/review-profile";
    }

    @GetMapping("/profile/wishlist")
    public String showProfileWishlist(Model model) {
        CustomUserDetails userInfo = SecurityUtil.getUserDetails();

        log.debug("profile wishlist find start = {}", userInfo.getMemberNo());
        try {

        } catch (Exception e) {

        }

        model.addAttribute(userInfo);

        return "member/wishlist-profile";
    }

    @GetMapping("/profile/point")
    public String showProfilePoint(Model model) {
        CustomUserDetails userInfo = SecurityUtil.getUserDetails();

        log.debug("profile point find start = {}", userInfo.getPoint());
        try {

        } catch (Exception e) {

        }

        model.addAttribute(userInfo);

        return "member/point-profile";
    }

    @GetMapping("/profile/edit")
    public String showProfileEdit(Model model) {
        CustomUserDetails userInfo = SecurityUtil.getUserDetails();

        log.info("profile edit = memberNO : {}, memberID {}", userInfo.getMemberNo(), userInfo.getUsername());

        model.addAttribute("userInfo", userInfo);

        return "member/edit-profile";
    }

    @ResponseBody
    @PostMapping("/api/profile/change-nickname")
    public boolean changeNickName(@RequestParam("nickName") String nickName) {
        log.info("log NickName request = {}", nickName);
        String type = "nickName";

        boolean result = true;

        try {
            memberService.changeMemberInfo("nickName", nickName);
        } catch (Exception e) {
            result = false;
        }

        return result;
    }

    @ResponseBody
    @PostMapping("/api/profile/change-password")
    public boolean changePassword(@RequestParam("password") String password) {
        log.info("log password request = {}", password);
        String type = "password";

        boolean result = true;

        try {
            memberService.changeMemberInfo(password);

        } catch (Exception e) {
            result = false;
        }

        return result;
    }

    @ResponseBody
    @PostMapping("/api/profile/change-email")
    public boolean changeEmail(@RequestParam("email") String email) {
        log.info("log email request = {}", email);
        String type = "email";

        boolean result = true;

        try {
            memberService.changeMemberInfo(email);
        } catch (Exception e) {
            result = false;
        }

        return result;
    }

//    @ResponseBody
//    @PostMapping("/api/check-email")
//    public boolean isEmail(@RequestParam("email") String email) {
//        System.out.println("테스트");
//        return authService.isMemberByEmail(email);
//    }

}
