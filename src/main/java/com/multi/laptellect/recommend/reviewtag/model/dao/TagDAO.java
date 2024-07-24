package com.multi.laptellect.recommend.reviewtag.model.dao;

import com.multi.laptellect.recommend.reviewtag.model.dto.TagDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TagDAO {
    void insertTag(TagDTO tag); // 새로운 태그를 데이터베이스에 삽입
    List<TagDTO> getAllTags();  // 모든 태그를 데이터베이스에서 조회

}
