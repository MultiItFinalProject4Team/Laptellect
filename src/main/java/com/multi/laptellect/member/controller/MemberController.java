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

    @GetMapping("/purchase")
    public String showProfilePurchase(Model model) {
        CustomUserDetails userInfo = SecurityUtil.getUserDetails();

        log.info("회원 정보 수정 페이지 log = memberNO : {}, memberID {}", userInfo.getMemberNo(), userInfo.getUsername());

        model.addAttribute(userInfo);

        return "/member/purchase-profile";
    }

    @GetMapping("/review")
    public String showProfileReview(Model model) {
        CustomUserDetails userInfo = SecurityUtil.getUserDetails();

        log.info("회원 정보 수정 페이지 log = memberNO : {}, memberID {}", userInfo.getMemberNo(), userInfo.getUsername());

        model.addAttribute(userInfo);

        return "/member/review-profile";
    }

    @GetMapping("/wishlist")
    public String showProfileWishlist(Model model) {
        CustomUserDetails userInfo = SecurityUtil.getUserDetails();

        log.info("회원 정보 수정 페이지 log = memberNO : {}, memberID {}", userInfo.getMemberNo(), userInfo.getUsername());

        model.addAttribute(userInfo);

        return "/member/wishlist-profile";
    }

    @GetMapping("/point")
    public String showProfilePoint(Model model) {
        CustomUserDetails userInfo = SecurityUtil.getUserDetails();

        log.info("회원 정보 수정 페이지 log = memberNO : {}, memberID {}", userInfo.getMemberNo(), userInfo.getUsername());

        model.addAttribute(userInfo);

        return "/member/point-profile";
    }

    @GetMapping("/edit")
    public String showProfileEdit(Model model) {
        CustomUserDetails userInfo = SecurityUtil.getUserDetails();

        log.info("회원 정보 수정 페이지 log = memberNO : {}, memberID {}", userInfo.getMemberNo(), userInfo.getUsername());

        model.addAttribute(userInfo);

        return "/member/edit-profile";
    }


}
