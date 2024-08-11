package com.multi.laptellect.recommend.txttag.model.dao;

import com.multi.laptellect.recommend.txttag.model.dto.ProductDTO2;
import com.multi.laptellect.recommend.txttag.model.dto.TaggDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface ProductTagDAO {
    void insertProductTag(int productNo, int tagNo);
    List<ProductDTO2> getAllProducts();
    List<TaggDTO> getAllTags();
    ArrayList<Integer> findTagByData(ProductDTO2 productDTO2);

}

