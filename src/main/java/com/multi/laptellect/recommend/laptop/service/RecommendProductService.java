package com.multi.laptellect.recommend.laptop.service;

import com.multi.laptellect.recommend.laptop.model.dao.RecommendProductDAO;
import com.multi.laptellect.recommend.laptop.model.dto.RecommendProductDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RecommendProductService {

    @Autowired
    private RecommendProductDAO recommendProductDAO; // RecommendProductDAO 인터페이스를 구현한 DAO 클래스를 주입받음

    @Autowired
    public RecommendProductService(RecommendProductDAO recommendProductDAO) {
        this.recommendProductDAO = recommendProductDAO;
    }

    public List<RecommendProductDTO> getRecommendations(Map<String, String> surveyResults) {
        Map<String, Object> searchCriteria = createSearchCriteria(surveyResults); // 설문 결과를 기반으로 검색 조건을 생성
        return recommendProductDAO.getRecommendedProducts(searchCriteria); // 생성된 검색 조건을 DAO에 전달하여 추천 상품 목록을 조회
    }

    private Map<String, Object> createSearchCriteria(Map<String, String> surveyResults) {
        Map<String, Object> criteria = new HashMap<>();

        String mainOption = surveyResults.get("mainOption");
        if ("게이밍".equals(mainOption)) {
            criteria.put("gpuTags", getGpuTags(surveyResults.get("game")));
        } else {
            criteria.put("cpuTags", getCpuTags(surveyResults.get("purpose")));
        }

        criteria.put("weightTag", getWeightTag(surveyResults.get("place"))); // 가벼운지 무거운지
        criteria.put("screenSizeTag", getScreenSizeTag(surveyResults.get("screen")));
        criteria.put("batteryTag", getBatteryTag(surveyResults.get("priority")));
        criteria.put("designTag", getDesignTag(surveyResults.get("priority")));
        criteria.put("performanceTag", getPerformanceTag(surveyResults.get("performance"))); // 성능 우선 순위가 높을 경우 성능 태그 부여

        return criteria;
    }
//일단 조건 해놨고 나머지는 차차 하드 코딩 예정
    private List<String> getGpuTags(String gameType) {
        switch (gameType) {
            case "스팀게임/FPS 게임":
                return List.of("GeForce RTX 4090", "GeForce RTX 4080", "Radeon RX 7900M");
            case "온라인 게임":
                return List.of("Radeon RX 6650M", "Radeon RX 6700S", "GeForce RTX 4050");
            case "AOS게임":
                return List.of("Quadro P5200", "Radeon RX 6850M", "GeForce RTX 2070");
            default:
                return List.of();
        }
    }

    private List<String> getCpuTags(String purpose) {
        switch (purpose) {
            case "코딩할거에요":
                return List.of("AMD Ryzen 9 7945HX3D", "Intel Core i9-13980HX", "AMD Ryzen 9 7845HX");
            case "학생이에요":
                return List.of("Intel Core i5-12500H", "AMD Ryzen 5 5600H", "Intel Core i7-1165G7");
            default:
                return List.of();
        }
    }

    private String getWeightTag(String place) {
        return "가져 다닐거에요".equals(place) ? "가벼워요" : "무거워요";
    }

    private String getScreenSizeTag(String screenPreference) {
        switch (screenPreference) {
            case "화면 넓은게 좋아요":
                return "넓은 화면";
            case "적당한게 좋아요":
                return "적당한 화면";
            default:
                return "작은 화면";
        }
    }

    private String getBatteryTag(String priority) { // 배터리 용량
        return "무게를 우선해주세요".equals(priority) ? "짧은 배터리" : "오래 가는 배터리";
    }

    private String getDesignTag(String priority) {
        return "화면을 우선해 주세요".equals(priority) ? "예쁜 디자인" : null; // 디자인 우선 순위 높을 경우 예쁜 디자인 태그 부여
    }

    private String getPerformanceTag(String performance) {
        return "성능용".equals(performance) ? "동세대 최고 성능" : null;
    }
}