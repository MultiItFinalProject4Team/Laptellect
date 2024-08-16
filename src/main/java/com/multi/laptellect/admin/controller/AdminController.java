package com.multi.laptellect.admin.controller;

import com.multi.laptellect.admin.model.dto.AdminDashboardDTO;
import com.multi.laptellect.admin.model.dto.AdminOrderlistDTO;
import com.multi.laptellect.admin.model.dto.AdminReviewDTO;
import com.multi.laptellect.admin.service.AdminService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping({"/dashboard", ""})
    public String admin_dashboard_page() {
        return "/admin/dashboard";
    }

    @GetMapping("/reviews_manage")
    public String reviews_manage_page(Model model) {
        List<AdminReviewDTO> reviewsAllList = adminService.selectAllReviews();
        model.addAttribute("reviewsAllList", reviewsAllList);
        return "/admin/reviews_manage";
    }

    @GetMapping("/orders_manage")
    public String orders_manage_page(Model model) {
        List<AdminOrderlistDTO> selectAllOrders = adminService.selectAllOrders();
        model.addAttribute("selectAllOrders", selectAllOrders);
        return "/admin/orders_manage";
    }

    @PostMapping("/deleteReviews")
    @ResponseBody
    public Map<String, Boolean> deleteReviews(@RequestBody List<Long> reviewIds) {
        boolean success = adminService.deleteReviews(reviewIds);
        return Map.of("success", success);
    }

    @GetMapping("/dashboard-data")
    @ResponseBody
    public List<AdminDashboardDTO> getDashboardData() {
        return adminService.getLastSevenDaysSales();
    }

    @GetMapping("/recent-reviews")
    @ResponseBody
    public List<AdminReviewDTO> getRecentReviews() {
        return adminService.getRecentReviews(20); // Get the 20 most recent reviews
    }
}