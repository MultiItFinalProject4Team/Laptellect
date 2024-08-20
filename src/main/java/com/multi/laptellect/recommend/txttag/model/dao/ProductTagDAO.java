package com.multi.laptellect.recommend.txttag.model.dao;

import com.multi.laptellect.recommend.txttag.model.dto.ProductTagDTO;
import com.multi.laptellect.recommend.txttag.model.dto.TaggDTO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface ProductTagDAO {

    @Insert("INSERT IGNORE INTO machine_tagkey (product_no, tag_no) VALUES (#{productNo}, #{tagNo})" )
    void insertProductTag(@Param("productNo") int productNo, @Param("tagNo") int tagNo);

    @Select("SELECT product_no FROM product WHERE type_no = 1")
    ArrayList<Integer> findAllProductNo();


    @Select("SELECT * FROM laptop_tag")
    List<TaggDTO> findAllTag();

    @Select("SELECT * FROM machine_tagkey")
    List<ProductTagDTO> getAllProductTags();

    @Select("SELECT * FROM laptop_tag WHERE tag_no = #{tagNo}")
    TaggDTO getTagById(@Param("tagNo") int tagNo);




}