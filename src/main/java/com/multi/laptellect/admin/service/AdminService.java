package com.multi.laptellect.admin.service;

import com.multi.laptellect.admin.model.dao.AdminDAO;
import com.multi.laptellect.admin.model.dto.AdminReviewDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminService {

    @Autowired
    private AdminDAO adminDAO;

    @Transactional(readOnly = true)
    public List<AdminReviewDTO> selectAllReviews() {
        return adminDAO.selectAllReviews();
    }

    @Transactional
    public boolean deleteReviews(List<Long> reviewIds) {
        try {
            adminDAO.deleteReviews(reviewIds);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}