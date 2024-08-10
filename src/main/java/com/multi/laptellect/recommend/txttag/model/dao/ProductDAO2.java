package com.multi.laptellect.recommend.txttag.model.dao;

import com.multi.laptellect.recommend.txttag.model.dto.ProductDTO2;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProductDAO2 {
    List<ProductDTO2> getAllProducts();
}
