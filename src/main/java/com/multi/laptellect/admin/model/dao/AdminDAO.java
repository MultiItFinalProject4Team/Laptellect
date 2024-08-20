package com.multi.laptellect.admin.model.dao;

import com.multi.laptellect.admin.model.dto.AdminDashboardDTO;
import com.multi.laptellect.admin.model.dto.AdminOrderlistDTO;
import com.multi.laptellect.admin.model.dto.AdminQuestionDTO;
import com.multi.laptellect.admin.model.dto.AdminReviewDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface AdminDAO {
    List<AdminReviewDTO> selectAllReviews();
    void deleteReviews(@Param("reviewIds") List<Long> reviewIds);

    List<AdminOrderlistDTO> selectAllOrders();

    List<AdminDashboardDTO> getLastSevenDaysSales();

    List<AdminDashboardDTO> getLastSevenDaysVisits();

    List<AdminReviewDTO> getRecentReviews(int limit);

    AdminDashboardDTO getTodayStats();

    List<AdminQuestionDTO> getRecentQuestions(int limit);
}