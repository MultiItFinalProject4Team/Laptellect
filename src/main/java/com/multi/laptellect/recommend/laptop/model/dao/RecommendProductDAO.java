package com.multi.laptellect.recommend.laptop.model.dao;

import com.multi.laptellect.product.model.dto.LaptopDetailsDTO;
import com.multi.laptellect.product.model.dto.laptop.LaptopSpecDTO;
import com.multi.laptellect.recommend.laptop.model.dto.ProductFilterDTO;
import com.multi.laptellect.recommend.laptop.model.dto.RecommendProductDTO;
import com.multi.laptellect.recommend.txttag.model.dto.TaggDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
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

    ArrayList<Integer> findLaptopDetailByFilter(ProductFilterDTO productFilterDTO); // 필터에 맞는 제품 조회

    @Select("SELECT pt.tag_no, lt.tag_data FROM machine_tagkey pt JOIN laptop_tag lt ON pt.tag_no = lt.tag_no WHERE pt.product_no = #{productNo}")
    List<TaggDTO> getTagsForProduct(@Param("productNo") int productNo);

    @Select("SELECT pt.tag_no, lt.tag_data FROM machine_tagkey pt JOIN laptop_tag lt ON pt.tag_no = lt.tag_no WHERE pt.product_no = #{productNo}")
    ArrayList<LaptopSpecDTO> getAllProducts(@Param("productNo") int productNo);
}
