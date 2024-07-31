package com.multi.laptellect.admin.model.dao;

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
}