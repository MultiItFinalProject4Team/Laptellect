package com.multi.laptellect.admin.service;

import com.multi.laptellect.admin.model.dao.AdminDAO;
import com.multi.laptellect.admin.model.dto.AdminOrderlistDTO;
import com.multi.laptellect.admin.model.dto.AdminReviewDTO;
import com.multi.laptellect.admin.model.dto.AdminDashboardDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final AdminDAO adminDAO;

    public AdminService(AdminDAO adminDAO) {
        this.adminDAO = adminDAO;
    }

    public List<AdminReviewDTO> selectAllReviews() {
        return adminDAO.selectAllReviews();
    }

    public boolean deleteReviews(List<Long> reviewIds) {
        try {
            adminDAO.deleteReviews(reviewIds);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<AdminOrderlistDTO> selectAllOrders() {
        return adminDAO.selectAllOrders();
    }

    public List<AdminDashboardDTO> getLastSevenDaysSales() {
        List<AdminDashboardDTO> salesData = adminDAO.getLastSevenDaysSales();

        Map<LocalDate, AdminDashboardDTO> salesMap = salesData.stream()
                .collect(Collectors.toMap(
                        dto -> LocalDate.parse(dto.getDate()),
                        dto -> dto
                ));

        List<AdminDashboardDTO> result = new ArrayList<>();
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");

        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            AdminDashboardDTO dto = salesMap.getOrDefault(date, new AdminDashboardDTO());
            dto.setDate(date.format(formatter));
            dto.setSaleCount(dto.getSaleCount());
            dto.setVisitCount(0); // 방문자 수는 현재 구현되어 있지 않으므로 0으로 설정
            result.add(dto);
        }

        return result;
    }
}