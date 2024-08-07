package com.multi.laptellect.recommend.score.model.dao;

import com.multi.laptellect.recommend.score.model.dto.DetailsDTO;
import com.multi.laptellect.recommend.score.model.dto.LaptopScoreDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface LaptopScoreDAO {
    List<DetailsDTO> getAllLaptops();
    Map<String, String> getLaptopSpecs(int productNo);
    void saveOrUpdateLaptopScore(LaptopScoreDTO laptopScoreDTO);
}
