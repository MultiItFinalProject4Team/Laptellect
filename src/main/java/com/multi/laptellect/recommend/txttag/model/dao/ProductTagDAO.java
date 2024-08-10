package com.multi.laptellect.recommend.txttag.model.dao;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductTagDAO {
    void deleteProductTags(int productNo);
    void insertProductTag(int productNo, int tagNo);
}

