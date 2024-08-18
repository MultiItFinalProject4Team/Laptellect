//package com.multi.laptellect.admin.visitor.controller;
//
//import com.multi.laptellect.admin.visitor.model.dto.VisitorLogDTO;
//import com.multi.laptellect.admin.visitor.service.AdminVisitorService;
//import com.multi.laptellect.common.model.PagebleDTO;
//import com.multi.laptellect.util.PaginationUtil;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.data.domain.Page;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//
///**
// * Please explain the class!!
// *
// * @author : 이강석
// * @fileName : AdminMemberController
// * @since : 2024-08-13
// */
//@Slf4j
//@Controller
//@RequiredArgsConstructor
//@RequestMapping("/admin/visitor")
//public class AdminVisitorController {
//    private final AdminVisitorService adminVisitorService;
//
//    @GetMapping("")
//    public String showVisit() {
//
//
//        return "";
//    }
//
//    @GetMapping("list")
//    public String getVisitList(@RequestBody PagebleDTO pagebleDTO, Model model) {
//        try {
//            Page<VisitorLogDTO> visitorLogs = adminVisitorService.getVisitorList(pagebleDTO);
//            log.info("방문자 로그 조회 성공 = {}", visitorLogs.getContent());
//
//            int page = visitorLogs.getPageable().getPageNumber();
//            int size = visitorLogs.getPageable().getPageSize();
//
//            int startPage = PaginationUtil.getStartPage(visitorLogs, 5);
//            int endPage = PaginationUtil.getEndPage(visitorLogs, 5);
//
//            model.addAttribute("visitorLogs", visitorLogs);
//            model.addAttribute("page", page);
//            model.addAttribute("size", size);
//            model.addAttribute("startPage", startPage);
//            model.addAttribute("endPage", endPage);
//        } catch (Exception e) {
//            log.error("방문자 로그 조회 실패");
//        }
//
//        return "";
//    }
//}
