package com.multi.laptellect.member.controller;

import com.multi.laptellect.member.model.dto.CustomUserDetails;
import com.multi.laptellect.member.service.MemberService;
import com.multi.laptellect.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/profile")
public class MemberController {
    private final MemberService memberService;

//    @GetMapping // 대시보드 시간 남으면 개발
//    public String showProfileDashboard(Model model) {
//        return "member/dashboard-profile";
//    }

    @GetMapping(value = {"", "/purchase"})
    public String showProfilePurchase(Model model) {
        CustomUserDetails userInfo = SecurityUtil.getUserDetails();

        log.debug("purchase find Start = {}", userInfo.getMemberNo());

        try {

        } catch (Exception e) {

        }

        model.addAttribute(userInfo);

        return "member/purchase-profile";
    }

    @GetMapping("/review")
    public String showProfileReview(Model model) {
        CustomUserDetails userInfo = SecurityUtil.getUserDetails();

        log.debug("profile review find Start = {}", userInfo.getMemberNo());
        try {

        } catch (Exception e) {

        }

        model.addAttribute(userInfo);

        return "member/review-profile";
    }

    @GetMapping("/wishlist")
    public String showProfileWishlist(Model model) {
        CustomUserDetails userInfo = SecurityUtil.getUserDetails();

        log.debug("profile wishlist find start = {}", userInfo.getMemberNo());
        try {

        } catch (Exception e) {

        }

        model.addAttribute(userInfo);

        return "member/wishlist-profile";
    }

    @GetMapping("/point")
    public String showProfilePoint(Model model) {
        CustomUserDetails userInfo = SecurityUtil.getUserDetails();

        log.debug("profile point find start = {}", userInfo.getPoint());
        try {

        } catch (Exception e) {

        }

        model.addAttribute(userInfo);

        return "member/point-profile";
    }

    @GetMapping("/edit")
    public String showProfileEdit(Model model) {
        CustomUserDetails userInfo = SecurityUtil.getUserDetails();

        log.info("profile edit = memberNO : {}, memberID {}", userInfo.getMemberNo(), userInfo.getUsername());

        model.addAttribute("userInfo", userInfo);

        return "member/edit-profile";
    }


}
