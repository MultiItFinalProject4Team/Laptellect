package com.multi.laptellect.recommend.txttag.model.dao;

import com.multi.laptellect.recommend.txttag.model.dto.TaggDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TagDAO2 {
    List<TaggDTO> getAllTags();
}
