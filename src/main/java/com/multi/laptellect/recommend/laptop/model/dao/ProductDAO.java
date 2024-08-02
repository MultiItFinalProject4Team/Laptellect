package com.multi.laptellect.recommend.laptop.model.dao;

import org.springframework.stereotype.Repository;
import com.multi.laptellect.recommend.laptop.model.dto.LaptopDTO;

import java.util.List;

@Repository
public interface ProductDAO {
    List<LaptopDTO> findByTags(List<String> tags);
}