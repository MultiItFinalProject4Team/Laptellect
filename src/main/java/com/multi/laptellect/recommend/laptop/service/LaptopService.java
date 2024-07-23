package com.multi.laptellect.recommend.laptop.service;

import com.multi.laptellect.recommend.laptop.model.dao.LaptopDAO;
import com.multi.laptellect.recommend.laptop.model.dto.LaptopDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class LaptopService {

    @Autowired
    private LaptopDAO laptopDAO;

    public List<String> getCategories() {
        return Arrays.asList("게이밍", "프로그래밍", "그래픽/디자인", "학생용");
    }

    public List<String> getCpuBrands() {
        return Arrays.asList("인텔", "라이젠");
    }

    public List<String> getGpuBrands() {
        return Arrays.asList("엔비디아", "AMD");
    }

    public List<String> getPriceRanges() {
        return Arrays.asList("50만원 이하", "100~200만원", "200만원 이상");
    }

    public List<LaptopDTO> getRecommendedLaptops(Map<String, Object> surveyResults) {
        List<String> tags = new ArrayList<>();

        addTagsFromList(tags, (List<String>) surveyResults.get("categories"));
        addTag(tags, (String) surveyResults.get("priceRange"));
        addTag(tags, (String) surveyResults.get("cpuBrand"));
        addTag(tags, (String) surveyResults.get("gpuBrand"));

        if (Boolean.TRUE.equals(surveyResults.get("asImportant"))) {
            tags.add("AS 중요");
        }

        return laptopDAO.findLaptopsByTags(tags);
    }

    private void addTagsFromList(List<String> tags, List<String> newTags) {
        if (newTags != null) {
            tags.addAll(newTags);
        }
    }

    private void addTag(List<String> tags, String tag) {
        if (tag != null && !tag.isEmpty()) {
            tags.add(tag);
        }
    }

    public Map<String, Object> getLaptopDetail(Long id) {
        return laptopDAO.findLaptopDetailById(id);
    }
}