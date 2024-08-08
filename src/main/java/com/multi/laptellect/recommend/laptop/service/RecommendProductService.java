package com.multi.laptellect.recommend.laptop.service;

import com.multi.laptellect.product.model.dto.LaptopDetailsDTO;
import com.multi.laptellect.product.model.dto.laptop.LaptopSpecDTO;
import com.multi.laptellect.product.model.mapper.ProductMapper;
import com.multi.laptellect.product.service.ProductService;
import com.multi.laptellect.recommend.laptop.model.dao.RecommendProductDAO;
import com.multi.laptellect.recommend.laptop.model.dto.CurationDTO;
import com.multi.laptellect.recommend.laptop.model.dto.ProductFilterDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class RecommendProductService {
    // Product
    private final ProductMapper productMapper; // 생성자 주입시 final
    private final ProductService productService;

    // Recommend
    private final RecommendProductDAO recommendProductDAO;

    public ArrayList<LaptopSpecDTO> getRecommendations(CurationDTO curationDTO) {
        //surveyResults는 사용자가 선택한 설문 결과를 담고 있는 맵
        ProductFilterDTO productFilterDTO = createSearchCriteria(curationDTO);
        log.info("큐레이션 조건 분류 완료 = {} " + productFilterDTO);

        // 조건에 따라 Product_no를 반환
        ArrayList<Integer> productNos = recommendProductDAO.findLaptopDetailByFilter(productFilterDTO);
        log.info("추천 결과 맞는 제품 번호 = {}", productNos);


        ArrayList<LaptopSpecDTO> laptop = new ArrayList<>();

        for(int productNo : productNos) {
            List<LaptopDetailsDTO> laptopDetails = productMapper.laptopProductDetails(productNo);

            log.info("상품 스펙 조회 = {}", laptopDetails);

            if (!laptopDetails.isEmpty()) {
                laptop.add(productService.getLaptopSpec(productNo, laptopDetails));
            } else {
                return null;
            }
        }
        return laptop;
    }


    private ProductFilterDTO createSearchCriteria(CurationDTO curationDTO) {
        ProductFilterDTO productFilterDTO = new ProductFilterDTO(); // 사용자가 원하는 조건이 담긴 DTO

        log.debug("큐레이션 조건 반환 시작 = {}", curationDTO);
        String mainOption = curationDTO.getMainOption();

        if (mainOption.equals("게이밍")) { // Key 값이 게임일 시 Gpu 중심
            String game = curationDTO.getGame();
            List<String> gpuValues = getGpuTags(game);
            productFilterDTO.setGpu(gpuValues);
        } else if (mainOption.equals("사무용")) { // key 값이 사무용일 시 CPU 중심
            String purpose = curationDTO.getPurpose();
            List<String> cpuValues = getCpuTags(purpose);
            productFilterDTO.setGpu(cpuValues);
        }

//        criteria.put("weightTag", getWeightTag(surveyResults.get("place")));
//        criteria.put("screenSizeTag", getScreenSizeTag(surveyResults.get("screen")));
//        criteria.put("batteryTag", getBatteryTag(surveyResults.get("priority")));
//        criteria.put("designTag", getDesignTag(surveyResults.get("priority")));
//        criteria.put("performanceTag", getPerformanceTag(surveyResults.get("performance")));

        return productFilterDTO;
    }

    private List<String> getGpuTags(String gameType) {
        //게임 타입이 null이면 빈 리스트 반환
        if (gameType == null) {
            return List.of();
        }
        //게임 타입에 따라 GPU 태그를 반환
        switch (gameType) {
            case "스팀게임/FPS 게임":
                return List.of(
                        "RTX4080", "RTX 4080", "라데온 RX 7900M", "라데온 610M Ryzen 9 7845HX",
                        "RTX3080 Ti", "RTX4070", "RTX3070 Ti", "RTX4060",
                        "라데온 RX 6850M XT", "RTX3080", "RTX A5000", "RTX3070",
                        "라데온 RX 6800S", "RTX A4000", "RTX2080, 라데온 RX 6700M"
                );
            case "온라인 게임":
                return List.of(
                        "라데온 RX 6650M", "라데온 RX 6700S", "쿼드로 RTX 5000", "RTX4050",
                        "라데온 RX 7600S", "라데온 RX 6850M XT", "RTX2080 SUPER", "RTX2070 SUPER",
                        "라데온 RX 6600M", "라데온 RX 6600S", "라데온 Pro W6600M", "라데온 RX 6700M",
                        "RTX3060", "라데온 RX 6800M", "RTX2080", "쿼드로 RTX 4000",
                        "RTX A3000", "RTX2070"
                );
            case "AOS게임":
                return List.of(
                        "쿼드로 P5200", "라데온 RX 6850M", "RTX2070", "GTX1080",
                        "라데온 RX 7600M XT", "인텔 Arc A770M", "RTX2060", "쿼드로 RTX 3000",
                        "GTX1070", "RTX3050", "GTX1660 Ti", "RTX A2000",
                        "라데온 RX 6550M", "라데온 Pro 5600M", "쿼드로 P4000", "라데온 RX 5600M"
                );
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
                return List.of("7945HX", "7940HX", "I9-13980HX",
                        "I9-14900HX", "7845HX", "I9-13900HX", "I9-13950HX",
                        "I7-14650HX", "I7-13850HX", "I7-14700HX", "I9-12900HX", "I7-13700HX",
                        "7745HX", "I9-12950HX", "I7-12800HX", "8945HS",
                        "I9-13900HK", "I7-13650HX", "I7-12850HX", "I5-1340P");
            case "학생이에요":
                return List.of("I5-12500H", "5600H", "I5-11400H",
                        "4600H", "I5-10300H", "4500U",
                        "I5-1135G7", "5500U", "I5-10210U",
                        "3500U", "I7-1165G7", "4700U",
                        "I7-10510U", "5700U", "I7-1185G7", "I5-1340P");
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