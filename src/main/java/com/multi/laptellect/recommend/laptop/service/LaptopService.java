package com.multi.laptellect.recommend.laptop.service;

import com.multi.laptellect.recommend.laptop.model.dao.LaptopDAO;
import com.multi.laptellect.recommend.laptop.model.dto.LaptopDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        List<LaptopDTO> allLaptops = laptopDAO.getAllLaptops();
        return allLaptops.stream()
                .filter(laptop -> matchesSurveyResults(laptop, surveyResults))
                .limit(5)  // 상위 5개 추천
                .collect(Collectors.toList());
    }

    private boolean matchesSurveyResults(LaptopDTO laptop, Map<String, Object> surveyResults) {
        boolean matches = true;

        // 카테고리 매칭
        List<String> categories = (List<String>) surveyResults.get("categories");
        if (categories != null && !categories.isEmpty()) {
            matches &= categories.stream()
                    .anyMatch(category -> laptop.getTags().contains(category));
        }

        // 가격대 매칭
        String priceRange = (String) surveyResults.get("priceRange");
        if (priceRange != null) {
            switch (priceRange) {
                case "50만원 이하":
                    matches &= laptop.getPrice() <= 500000;
                    break;
                case "100~200만원":
                    matches &= laptop.getPrice() > 1000000 && laptop.getPrice() <= 2000000;
                    break;
                case "200만원 이상":
                    matches &= laptop.getPrice() > 2000000;
                    break;
            }
        }

        // CPU 브랜드 매칭
        String cpuBrand = (String) surveyResults.get("cpuBrand");
        if (cpuBrand != null) {
            matches &= laptop.getCpu().toLowerCase().contains(cpuBrand.toLowerCase());
        }

        // GPU 브랜드 매칭
        String gpuBrand = (String) surveyResults.get("gpuBrand");
        if (gpuBrand != null) {
            matches &= laptop.getGpu().toLowerCase().contains(gpuBrand.toLowerCase());
        }

        // AS 중요도 매칭
        Boolean asImportant = (Boolean) surveyResults.get("asImportant");
        if (asImportant != null && asImportant) {
            matches &= laptop.getTags().contains("AS 중요");
        }

        return matches;
    }
}