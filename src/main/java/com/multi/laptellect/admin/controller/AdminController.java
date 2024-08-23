package com.multi.laptellect.admin.controller;

import com.multi.laptellect.admin.model.dto.AdminDashboardDTO;
import com.multi.laptellect.admin.model.dto.AdminOrderlistDTO;
import com.multi.laptellect.admin.model.dto.AdminQuestionDTO;
import com.multi.laptellect.admin.model.dto.AdminReviewDTO;
import com.multi.laptellect.admin.service.AdminService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * The type Admin controller.
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    /**
     * AdminService 생성자
     *
     * @param adminService the admin service
     */
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    /**
     * 대쉬보드페이지
     *
     * @return the string
     */
    @GetMapping({"/dashboard", ""})
    public String admin_dashboard_page() {
        return "admin/dashboard";
    }

    /**
     * 리뷰관리페이지
     *
     * @param model the model
     * @return the string
     */
    @GetMapping("/reviews_manage")
    public String reviews_manage_page(Model model) {
        List<AdminReviewDTO> reviewsAllList = adminService.selectAllReviews();
        model.addAttribute("reviewsAllList", reviewsAllList);
        return "admin/reviews_manage";
    }

    /**
     * 주문관리페이지
     *
     * @param model the model
     * @return the string
     */
    @GetMapping("/orders_manage")
    public String orders_manage_page(Model model) {
        List<AdminOrderlistDTO> selectAllOrders = adminService.selectAllOrders();
        model.addAttribute("selectAllOrders", selectAllOrders);
        return "admin/orders_manage";
    }

    /**
     * 리뷰관리 - 리뷰삭제메서드
     *
     * @param reviewIds the review ids
     * @return the map
     */
    @PostMapping("/deleteReviews")
    @ResponseBody
    public Map<String, Boolean> deleteReviews(@RequestBody List<Long> reviewIds) {
        boolean success = adminService.deleteReviews(reviewIds);
        return Map.of("success", success);
    }

    /**
     * 대쉬보드 페이지를 구성하는 대시보드dto
     *
     * @return the dashboard data
     */
    @GetMapping("/dashboard-data")
    @ResponseBody
    public List<AdminDashboardDTO> getDashboardData() {
        return adminService.getLastSevenDaysSales();
    }

    /**
     * 대시보드 리뷰탭에 있는 최근 리뷰20개를 불러오는 매서드
     *
     * @return the recent reviews
     */
    @GetMapping("/recent-reviews")
    @ResponseBody
    public List<AdminReviewDTO> getRecentReviews() {
        return adminService.getRecentReviews(20); // Get the 20 most recent reviews
    }


    /**
     * 대시보드 문의탭에 있는 최근 문의 20개를 불러오는 매서드
     *
     * @return the recent questions
     */
    @GetMapping("/recent-questions")
    @ResponseBody
    public List<AdminQuestionDTO> getRecentQuestions() {
        return adminService.getRecentQuestions(20); // Get the 20 most recent questions
    }

}