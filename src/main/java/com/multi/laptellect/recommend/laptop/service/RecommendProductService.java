package com.multi.laptellect.recommend.laptop.service;

import com.multi.laptellect.product.model.dto.LaptopDetailsDTO;
import com.multi.laptellect.product.model.dto.laptop.LaptopSpecDTO;
import com.multi.laptellect.product.service.ProductService;
import com.multi.laptellect.recommend.laptop.model.dao.RecommendProductDAO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class RecommendProductService {



    private final RecommendProductDAO recommendProductDAO; //private final 자동적 생성자 주입
    private final ProductService productService; // 생성자 주입시 final


    public ArrayList<LaptopSpecDTO> getRecommendations(Map<String, String> surveyResults) {
        //surveyResults는 사용자가 선택한 설문 결과를 담고 있는 맵
        Map<String, Object> searchCriteria = createSearchCriteria(surveyResults);
        log.info("큐레이션 조건 반환 = {} " + searchCriteria);

        ArrayList<LaptopDetailsDTO> laptopDetails = recommendProductDAO.findLaptopDetailByCriteria(searchCriteria); //설문 결과에 따라 추천 제품을 찾음

        log.info("모든 노트북 상세정보 = {} " + laptopDetails); //이게 주석 대신할 로그 림복 어노테이션이랑 연결 됨


//
//        ArrayList<LaptopSpecDTO> laptopSpecList = new ArrayList<>(); // 상세 정보 정리된 DTO 리스트
//
//
//        List<Integer> productNos = recommendProductDAO.findAllProductNo();
//        log.info("노트북 넘버 = {} ", productNos);
//
//
//        for (int i = 0; i < productNos.size(); i++) {
//            int productNo = productNos.get(i);
//            laptopSpecList.add(productService.getLaptopSpec(productNo, laptopDetails));
//            log.info("LaptopSpecDTO 반환 성공");
//        }
//        log.info("반환 완료 = {}", laptopSpecList);

        return null;
    }


    private Map<String, Object> createSearchCriteria(Map<String, String> surveyResults) {
        Map<String, Object> criteria = new HashMap<>();


        log.debug("큐레이션 조건 반환 시작 = {}", surveyResults);
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
        //게임 타입이 null이면 빈 리스트 반환
        if (gameType == null) {
            return List.of();
        }
        //게임 타입에 따라 GPU 태그를 반환
        switch (gameType) {
            case "스팀게임/FPS 게임":
                return List.of("RTX4080", "geforce rtx 4080", "radeon rx 7900m", "radeon 610m ryzen 9 7845hx",
                        "geforce rtx 3080 ti", "geforce rtx 4070", "geforce rtx 3070 ti", "geforce rtx 4060",
                        "radeon rx 6850m xt", "geforce rtx 3080", "rtx a5000", "geforce rtx 3070",
                        "radeon rx 6800s", "rtx a4000", "geforce rtx 2080, radeon rx 6700m");
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
        //사용 목적이 null이면 빈 리스트 반환
        if (purpose == null) {
            return List.of();
        }
        //사용 목적에 따라 CPU 태그를 반환
        switch (purpose) { //
            case "코딩할거에요":
                return List.of("AMD RYZEN 9 7945HX3D", "AMD RYZEN 9 7945HX", "AMD RYZEN 9 7940HX", "I9-13980HX",
                        "I9-14900HX", "AMD RYZEN 9 7845HX",
                        "I9-13900HX", "I9-13950HX", "I7-14650HX", "I7-13850HX", "I7-14700HX", "I9-12900HX", "I7-13700HX",
                        "AMD RYZEN 7 7745HX", "I9-12950HX", "I7-12800HX", "AMD 8945H", "I9-13900HK", "I7-13650HX", "I7-12850HX , I5-1340P");
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