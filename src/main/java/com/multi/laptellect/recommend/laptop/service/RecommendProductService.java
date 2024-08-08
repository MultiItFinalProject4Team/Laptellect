package com.multi.laptellect.recommend.laptop.service;

import com.multi.laptellect.recommend.laptop.model.dao.RecommendProductDAO;
import com.multi.laptellect.recommend.laptop.model.dto.RecommendProductDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class RecommendProductService {


    private final RecommendProductDAO recommendProductDAO; //private final 자동적 생성자 주입

    public List<RecommendProductDTO> getRecommendations(Map<String, String> surveyResults) {
        Map<String, Object> searchCriteria = createSearchCriteria(surveyResults);
        System.out.println("Search Criteria: " + searchCriteria);
        return recommendProductDAO.getRecommendedProducts(searchCriteria);
    }

    private Map<String, Object> createSearchCriteria(Map<String, String> surveyResults) {
        Map<String, Object> criteria = new HashMap<>();



        if (surveyResults.containsKey("game")) {
            criteria.put("gpuTags", getGpuTags(surveyResults.get("game")));
        } else if (surveyResults.containsKey("purpose")) {
            criteria.put("cpuTags", getCpuTags(surveyResults.get("purpose")));
        }

//        criteria.put("weightTag", getWeightTag(surveyResults.get("place")));
//        criteria.put("screenSizeTag", getScreenSizeTag(surveyResults.get("screen")));
//        criteria.put("batteryTag", getBatteryTag(surveyResults.get("priority")));
//        criteria.put("designTag", getDesignTag(surveyResults.get("priority")));
//        criteria.put("performanceTag", getPerformanceTag(surveyResults.get("performance")));


        return criteria;
    }

    private List<String> getGpuTags(String gameType) {
        if (gameType == null) {
            return List.of();
        }
        switch (gameType) {
            case "스팀게임/FPS 게임":
                return List.of("geforce rtx 4090", "geforce rtx 4080", "radeon rx 7900m", "radeon 610m ryzen 9 7845hx",
                        "geforce rtx 3080 ti", "geforce rtx 4070", "geforce rtx 3070 ti", "geforce rtx 4060",
                        "radeon rx 6850m xt", "geforce rtx 3080", "rtx a5000", "geforce rtx 3070",
                        "radeon rx 6800s", "rtx a4000", "geforce rtx 2080, radeon rx 6700m, ");
            case "온라인 게임":
                return List.of("radeon rx 6650m", "radeon rx 6700s", "quadro rtx 5000", "geforce rtx 4050",
                        "radeon rx 7600s", "radeont rx 6850m xt", "geforce rtx 2080 super", "geforce rtx 2070 super",
                        "radeon rx 6600m", "radeon rx 6600s", "radeon pro w6600m", "radeon rx 6700m",
                        "geforce rtx 3060", "radeon rx 6800m", "geforce rtx 2080", "quadro rtx 4000",
                        "rtx a3000", "geforce rtx 2070");
            case "AOS게임":
                return List.of("quadro p5200", "radeon rx 6850m", "geforce rtx 2070", "geforce gtx 1080",
                        "radeon rx 7600m xt", "intel arc a770m", "geforce rtx 2060", "quadro rtx 3000",
                        "geforce gtx 1070", "geforce rtx 3050", "geforce gtx 1660 ti", "rtx a2000",
                        "radeon rx 6550m", "radeon pro 5600m", "quadro p4000", "radeon rx 5600m");
            default:
                return List.of();
        }
    }

    private List<String> getCpuTags(String purpose) {
        if (purpose == null) {
            return List.of();
        }
        switch (purpose) { //
            case "코딩할거에요":
                return List.of("amd ryzen 9 7945hx3d", "amd ryzen 9 7945hx", "amd ryzen 9 7940hx",
                        "intel core i9-13980hx", "intel core i9-14900hx", "amd ryzen 9 7845hx",
                        "intel core i9-13900hx", "intel core i9-13950hx", "intel core i7-14650hx",
                        "intel core i7-13850hx", "intel core i7-14700hx", "intel core i9-12900hx",
                        "intel core i7-13700hx", "amd ryzen 7 7745hx", "intel core i9-12950hx",
                        "intel core i7-12800hx", "amd ryzen 9 8945h", "intel core i9-13900hk",
                        "intel core i7-13650hx", "intel core i7-12850hx , intel core i5-1340p");
            case "학생이에요":
                return List.of("intel core i5-12500h", "amd ryzen 5 5600h", "intel core i5-11400h",
                        "amd ryzen 5 4600h", "intel core i5-10300h", "amd ryzen 5 4500u",
                        "intel core i5-1135g7", "amd ryzen 5 5500u", "intel core i5-10210u",
                        "amd ryzen 5 3500u", "intel core i7-1165g7", "amd ryzen 7 4700u",
                        "intel core i7-10510u", "amd ryzen 7 5700u", "intel core i7-1185g7, intel core i5-1340p");
            default:
                return List.of();
        }
    }

//    private String getWeightTag(String place) {
//        return "가져 다닐거에요".equals(place) ? "가벼워요" : "무거워요";
//    }
//
//    private String getPerformanceTag(String performance) {
//        if (performance == null) {
//            return null;
//        }
//        switch (performance) {
//            case "성능용":
//                return "동세대 최고 성능";
//            case "가성비용":
//                return "가성비";
//            case "밸런스용":
//                return "밸런스";
//            default:
//                return null;
//        }
//    }
//
//    private String getBatteryTag(String priority) {
//        return "무게를 우선해주세요".equals(priority) ? "짧은 배터리" : "오래 가는 배터리";
//    }
//
//    private String getDesignTag(String priority) {
//        return "화면을 우선해 주세요".equals(priority) ? "예쁜 디자인" : null;
//    }
//
//    private String getScreenSizeTag(String screen) {
//        if (screen == null) {
//            return null;
//        }
//        switch (screen) {
//            case "화면 넓은게 좋아요":
//                return "17인치 이상";
//            case "적당한게 좋아요":
//                return "15인치";
//            case "알아서":
//                return "13인치 이하";
//            default:
//                return null;
//        }
//    }
}