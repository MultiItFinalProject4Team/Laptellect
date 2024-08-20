package com.multi.laptellect.admin.service;

import com.multi.laptellect.admin.model.dao.AdminDAO;
import com.multi.laptellect.admin.model.dto.AdminDashboardDTO;
import com.multi.laptellect.admin.model.dto.AdminOrderlistDTO;
import com.multi.laptellect.admin.model.dto.AdminQuestionDTO;
import com.multi.laptellect.admin.model.dto.AdminReviewDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
        List<AdminDashboardDTO> visitsData = adminDAO.getLastSevenDaysVisits();

        Map<String, AdminDashboardDTO> resultMap = new HashMap<>();

        for (AdminDashboardDTO sale : salesData) {
            resultMap.put(sale.getDate(), sale);
        }

        for (AdminDashboardDTO visit : visitsData) {
            AdminDashboardDTO dto = resultMap.get(visit.getDate());
            if (dto != null) {
                dto.setVisitCount(visit.getVisitCount());
            } else {
                resultMap.put(visit.getDate(), visit);
            }
        }

        List<AdminDashboardDTO> result = new ArrayList<>(resultMap.values());
        Collections.sort(result, Comparator.comparing(AdminDashboardDTO::getDate));

        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");

        List<AdminDashboardDTO> finalResult = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            String formattedDate = date.format(formatter);
            AdminDashboardDTO adminDashboardDTO = resultMap.getOrDefault(formattedDate, new AdminDashboardDTO());
            adminDashboardDTO.setDate(formattedDate);
            if (adminDashboardDTO.getSaleCount() == 0) {
                adminDashboardDTO.setSaleCount(0);
            }
            if (adminDashboardDTO.getVisitCount() == 0) {
                adminDashboardDTO.setVisitCount(0);
            }
            finalResult.add(adminDashboardDTO);
        }

        // 오늘의 추가 데이터 가져오기
        AdminDashboardDTO todayData = adminDAO.getTodayStats();

        // 결과의 마지막 항목(오늘)에 추가 데이터 설정
        if (!finalResult.isEmpty()) {
            AdminDashboardDTO todayDTO = finalResult.get(finalResult.size() - 1);
            todayDTO.setNewMemberCount(todayData.getNewMemberCount());
            todayDTO.setReviewCount(todayData.getReviewCount());
            todayDTO.setInquiryCount(todayData.getInquiryCount());
        }

        return finalResult;
    }

    public List<AdminReviewDTO> getRecentReviews(int limit) {
        return adminDAO.getRecentReviews(limit);
    }


    public List<AdminQuestionDTO> getRecentQuestions(int limit) {
        return adminDAO.getRecentQuestions(limit);
    }


}