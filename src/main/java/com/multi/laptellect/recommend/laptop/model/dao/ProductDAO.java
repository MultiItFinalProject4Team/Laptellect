package com.multi.laptellect.recommend.laptop.model.dao;

import com.multi.laptellect.recommend.laptop.model.dto.LaptopDTO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface ProductDAO {
    List<LaptopDTO> findAllLaptops();
    List<String> findTagsByProductId(int productNo);
}