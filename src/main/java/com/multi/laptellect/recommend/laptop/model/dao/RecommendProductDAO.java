package com.multi.laptellect.recommend.laptop.model.dao;

import com.multi.laptellect.product.model.dto.LaptopDetailsDTO;
import com.multi.laptellect.recommend.laptop.model.dto.RecommendProductDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Mapper
public interface RecommendProductDAO {
    List<RecommendProductDTO> getRecommendedProducts(Map<String, Object> criteria);

    @Select("SELECT * FROM vw_product_detail")
    ArrayList<LaptopDetailsDTO> findAllLaptopDetail();

    @Select("SELECT product_no FROM product")
    List<Integer> findAllProductNo();

    ArrayList<LaptopDetailsDTO> findLaptopDetailByCriteria(Map<String, Object> searchCriteria);
}
