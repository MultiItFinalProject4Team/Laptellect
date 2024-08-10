package com.multi.laptellect.recommend.reviewtag.model.dao;

import com.multi.laptellect.recommend.reviewtag.model.dto.TagDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TagDAO {
    void insertTag(TagDTO tag);
    List<TagDTO> getAllTags();
    List<String> getAllReviews();
}