package com.multi.laptellect.recommend.txttag.model.dao;

import com.multi.laptellect.recommend.txttag.model.dto.TaggDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface ProductTagDAO {
    void insertProductTag(int productNo, List<Integer> tagNo);

    @Select("SELECT product_no FROM product WHERE type_no = 1")
    ArrayList<Integer> findAllProductNo(); //find = select , all = 모든 , product_no = 제품번호


    @Select("SELECT * FROM laptop_tag")
    List<TaggDTO> findAllTag();

}
//인터페이스에서는 변수명을 안 쓴다
//모든 객체 이름은 다 의미가 있다
//findAllProductNo 객체