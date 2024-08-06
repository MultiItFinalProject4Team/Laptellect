package com.multi.laptellect.recommend.txttag.model.dao;

public interface ProductTagDAO {
    void deleteProductTags(int productNo);
    void insertProductTag(int productNo, int tagNo);
}

