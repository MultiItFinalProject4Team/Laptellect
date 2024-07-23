package com.multi.laptellect.recommend.laptop.model.dao;

import com.multi.laptellect.recommend.laptop.model.dto.LaptopDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface LaptopDAO {
    List<LaptopDTO> findLaptopsByTags(@Param("tags") List<String> tags);
    Map<String, Object> findLaptopDetailById(@Param("id") Long id);
}