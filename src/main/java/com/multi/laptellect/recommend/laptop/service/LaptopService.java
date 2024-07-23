package com.multi.laptellect.recommend.laptop.service;


import com.multi.laptellect.recommend.laptop.model.dao.LaptopDAO;
import com.multi.laptellect.recommend.laptop.model.dto.LaptopDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class LaptopService {

    @Autowired
    private LaptopDAO laptopDAO;

    public List<LaptopDTO> getRecommendedLaptops(List<String> tage) {
        return  laptopDAO.findLaptopsByTags(tage);
    }
}
