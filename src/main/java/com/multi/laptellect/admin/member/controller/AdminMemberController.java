package com.multi.laptellect.admin.member.controller;

import com.multi.laptellect.admin.member.model.dto.AdminMemberDTO;
import com.multi.laptellect.admin.member.service.AdminMemberService;
import com.multi.laptellect.admin.model.dto.LoginLog;
import com.multi.laptellect.common.model.PagebleDTO;
import com.multi.laptellect.member.model.dto.MemberDTO;
import com.multi.laptellect.member.service.MemberService;
import com.multi.laptellect.util.PaginationUtil;
import com.multi.laptellect.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Please explain the class!!
 *
 * @author : 이강석
 * @fileName : AdminMemberController
 * @since : 2024-08-13
 */
@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/member")
public class AdminMemberController {
    private final AdminMemberService adminMemberService;
    private final MemberService memberService;
    private final RedisUtil redisUtil;

    @GetMapping({""})
    public String showMemberManagement(Model model) {

        try {
            int newMemberCount = adminMemberService.findNewMemberCount(); // 금일 가입자 수
            int memberCount = adminMemberService.findMemberCount(); // 금일 가입자 수
            int activeMemberCount = redisUtil.getActiveUserCount(); // 현재 접속중인 인원 수

            model.addAttribute("memberCount", memberCount);
            model.addAttribute("newMemberCount", newMemberCount);
            model.addAttribute("activeMemberCount", activeMemberCount);
        } catch (Exception e) {
            log.error("Member Mgt Error = ", e);
        }

        return "/admin/member/member-manage";
    }

    @PostMapping("/list")
    public String getMemberList(@RequestBody PagebleDTO pagebleDTO, Model model) {

        try {
            log.debug("어드민 멤버 내역 전체 조회 시작");
            Page<AdminMemberDTO> members = adminMemberService.getMemberList(pagebleDTO);
            log.info("어드민 멤버 내역 전체 조회 성공 = {}", members.getContent());

            int page = members.getPageable().getPageNumber();
            int size = members.getPageable().getPageSize();

            int startPage = PaginationUtil.getStartPage(members, 5);
            int endPage = PaginationUtil.getEndPage(members, 5);

            model.addAttribute("members", members);
            model.addAttribute("page", page);
            model.addAttribute("size", size);
            model.addAttribute("startPage", startPage);
            model.addAttribute("endPage", endPage);

        } catch (Exception e) {
            log.error("어드민 멤버 내역 전체 조회 실패");
        }

        return "/admin/member/member-list";
    }

    @PostMapping("/member-info")
    public String getMember(@RequestParam(name = "memberNo") int memberNo, Model model) {
        try {
            MemberDTO member = adminMemberService.findMemberByMemberNo(memberNo);
            LoginLog loginLog = adminMemberService.findLoginLogByMemberNo(memberNo);
            model.addAttribute("userInfo", member);
            model.addAttribute("loginLog", loginLog);
        } catch (Exception e) {
            log.info("멤버 조회 실패 = {}", memberNo);
        }
        return "/admin/member/member-info";
    }

    @ResponseBody
    @PostMapping("/update")
    public int updateMember(@RequestParam MemberDTO memberDTO) {
        try {

        } catch (Exception e) {

        }
        return 0;
    }

    @ResponseBody
    @PostMapping("/delete")
    public int deleteMember(@RequestParam(name = "memberNo") int memberNo) {
        log.debug("회원 삭제 시작");
        try {
            return adminMemberService.deleteMember(memberNo);
        } catch (Exception e) {
            log.error("회원 삭제 실패");
            return 0;
        }
    }
}
