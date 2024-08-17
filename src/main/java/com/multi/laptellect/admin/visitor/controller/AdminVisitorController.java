package com.multi.laptellect.admin.visitor.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

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
@RequestMapping("/admin/visitor")
public class AdminVisitorController {
//    private final AdminMemberService adminMemberService;
//    private final RedisUtil redisUtil;
//
//    @GetMapping({""})
//    public String showMemberManagement(Model model) {
//
//        try {
//            int newMemberCount = adminMemberService.findNewMemberCount(); // 금일 가입자 수
//            int memberCount = adminMemberService.findMemberCount(); // 금일 가입자 수
//            int activeMemberCount = redisUtil.getActiveUserCount(); // 현재 접속중인 인원 수
//
//            model.addAttribute("memberCount", memberCount);
//            model.addAttribute("newMemberCount", newMemberCount);
//            model.addAttribute("activeMemberCount", activeMemberCount);
//        } catch (Exception e) {
//            log.error("Member Mgt Error = ", e);
//        }
//
//        return "/admin/member/member-manage";
//    }
//
//    @PostMapping("/log")
//    public String getMemberList(@RequestBody PagebleDTO pagebleDTO, Model model) {
//
//        try {
//            log.debug("어드민 멤버 내역 전체 조회 시작");
//            Page<AdminMemberDTO> members = adminMemberService.getMemberList(pagebleDTO);
//            log.info("어드민 멤버 내역 전체 조회 성공 = {}", members.getContent());
//
//            int page = members.getPageable().getPageNumber();
//            int size = members.getPageable().getPageSize();
//
//            int startPage = PaginationUtil.getStartPage(members, 5);
//            int endPage = PaginationUtil.getEndPage(members, 5);
//
//            model.addAttribute("members", members);
//            model.addAttribute("page", page);
//            model.addAttribute("size", size);
//            model.addAttribute("startPage", startPage);
//            model.addAttribute("endPage", endPage);
//        } catch (Exception e) {
//            log.error("어드민 멤버 내역 전체 조회 실패");
//        }
//
//        return "/admin/member/member-list";
//    }
}
