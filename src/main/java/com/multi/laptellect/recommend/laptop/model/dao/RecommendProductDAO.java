package com.multi.laptellect.recommend.laptop.model.dao;

import com.multi.laptellect.recommend.laptop.model.dto.RecommendProductDTO;

import java.util.List;
import java.util.Map;

public interface RecommendProductDAO {
    List<RecommendProductDTO> getRecommendedProducts(Map<String, Object> criteria);
}
