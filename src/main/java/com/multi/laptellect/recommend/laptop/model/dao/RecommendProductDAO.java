package com.multi.laptellect.recommend.laptop.model.dao;

import com.multi.laptellect.recommend.laptop.model.dto.RecommendProductDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface RecommendProductDAO {
    List<RecommendProductDTO> getRecommendedProducts(Map<String, Object> criteria);
}
