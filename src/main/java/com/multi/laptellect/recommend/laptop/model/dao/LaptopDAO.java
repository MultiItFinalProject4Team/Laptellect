package com.multi.laptellect.recommend.laptop.model.dao;

import com.multi.laptellect.recommend.laptop.model.dto.LaptopDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LaptopDAO {
    //태그 목록으로 노트북 검색
    List<LaptopDTO> findLaptopsByTags(@Param("tags") List<String> tags);
//    Map<String, Object> findLaptopDetailById(@Param("id") Long id);
    //나중에 상세 정보도 넣을 것 같아서 일단 넣어둠
}