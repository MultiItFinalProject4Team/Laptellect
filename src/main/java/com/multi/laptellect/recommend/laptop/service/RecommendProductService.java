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
    private final ProductMapper productMapper; // 생성자 주입시 final // ProductMapper는 노트북 상세 정보를 담는다
    private final ProductService productService; // 생성자 주입시 final // ProductService는 노트북 스펙을 담는다

    // Recommend
    private final RecommendProductDAO recommendProductDAO;

    public ArrayList<LaptopSpecDTO> getRecommendations(CurationDTO curationDTO) {
        //surveyResults는 사용자가 선택한 설문 결과를 담고 있는 맵
        ProductFilterDTO productFilterDTO = createSearchCriteria(curationDTO);
        log.info("큐레이션 조건 분류 완료 = {} " + productFilterDTO);

        // 조건에 따라 Product_no를 반환
        ArrayList<Integer> productNos = recommendProductDAO.findLaptopDetailByFilter(productFilterDTO);
        log.info("추천 결과 맞는 제품 번호 = {}", productNos);


        ArrayList<LaptopSpecDTO> laptop = new ArrayList<>(); // 사용자에게 추천할 노트북 리스트 // 노트북 스펙을 담음

        for(int productNo : productNos) {
            List<LaptopDetailsDTO> laptopDetails = productMapper.laptopProductDetails(productNo); //노트북 상세 정보 조회

            log.info("상품 스펙 조회 = {}", laptopDetails); // 로그

            if (!laptopDetails.isEmpty()) { //상세 정보 여부
                laptop.add(productService.getLaptopSpec(productNo, laptopDetails)); //노트북 스팩을 담는다
            } else {
                return null;
            }
        }
        return laptop;
    }


    private ProductFilterDTO createSearchCriteria(CurationDTO curationDTO) {
        ProductFilterDTO productFilterDTO = new ProductFilterDTO(); // 사용자가 원하는 조건이 담긴 DTO

        log.debug("큐레이션 조건 반환 시작 = {}", curationDTO);
        String mainOption = curationDTO.getMainOption(); // key 값이 게임, 사무용, 장소 등 구분 공통 요소

        if (mainOption.equals("게임 할거에요")) { //키 값이 게임일 시 Gpu 중심
            String game = curationDTO.getGame(); // 게임 타입
            List<String> gpuValues = getGpuTags(game); // 게임 타입에 따라 gpu 태그 반환
            productFilterDTO.setGpu(gpuValues); //gpu태그 설정
        } else if (mainOption.equals("작업 할거에요")) { // key 값이 사무용일 시 cpu 중심
            String purpose = curationDTO.getPurpose(); // 사용 목적 (코드 작업, AI 작업)
            List<String> cpuValues = getCpuTags(purpose);
            productFilterDTO.setGpu(cpuValues);
        }else if (mainOption.equals("문서나 인강 볼거에요")){
            String internet = curationDTO.getInternet();
            List<String> internetValues = getInternetTag(internet);
            productFilterDTO.setInternet(internetValues);
        }




      String place = curationDTO.getPlace(); // key 값이 장소일 시 무게 중심
        List<String> placeValues = getPlace(place); // 장소에 따른 무게 태그 반환
        productFilterDTO.setPlace(placeValues); //무게 태그 설정

        String performance = curationDTO.getPerformance(); // key 값이 성능일 시 가격 중심
        int[] priceRange = getPriceRange(performance); // 성능에 따른 가격 범위 반환
        productFilterDTO.setMinPrice(priceRange[0]); //최소 가격 설정
        productFilterDTO.setMaxPrice(priceRange[1]); //최대 가격 설정

        String screen = curationDTO.getScreen(); // key 값이 화면일 시 화면 중심
        List<String> screenValues = getScreenSizeTags(screen);
        productFilterDTO.setScreen(screenValues);

//        String battery = curationDTO.getBattery();
//        List<String> batteryValues = getBatteryTag(battery);
//        productFilterDTO.setBattery(batteryValues);



//        criteria.put("batteryTag", getBatteryTag(surveyResults.get("priority")));
//        criteria.put("designTag", getDesignTag(surveyResults.get("priority")));
//        criteria.put("performanceTag", getPerformanceTag(surveyResults.get("performance")));

//        String gameperformance = curationDTO.getGameperformance();
//        int[] gamepriceRange = getGameperformance(gameperformance);
//        productFilterDTO.setMinGamePrice(gamepriceRange[0]);
//        productFilterDTO.setMaxGamePrice(gamepriceRange[1]);

        return productFilterDTO;
    }
// 게임 타입에 따라 gpu태그를 반환
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
//사용 목적에 따라 cpu 태그를 반환
    private List<String> getCpuTags(String purpose) {
        //사용 목적이 null이면 빈 리스트 반환
        if (purpose == null) {
            return List.of();
        }
        //사용 목적에 따라 CPU 태그를 반환
        switch (purpose) { //
            case "코드 작업할거에요": //맥북 시리즈 추가 예정
                return List.of("7945HX", "7940HX", "I9-13980HX",
                        "I9-14900HX", "7845HX", "I9-13900HX", "I9-13950HX",
                        "I7-14650HX", "I7-13850HX", "I7-14700HX", "I9-12900HX", "I7-13700HX",
                        "7745HX", "I9-12950HX", "I7-12800HX", "8945HS",
                        "I9-13900HK", "I7-13650HX", "I7-12850HX", "I5-1340P"
                );
            case "AI 작업할거에요": //테스트를 위해 임시 Intel Xeon 라인 및 엔디비아 Quadro 라인 추가 될 예정
                return List.of("I5-12500H", "5600H", "I5-11400H",
                        "4600H", "I5-10300H", "4500U",
                        "I5-1135G7", "5500U", "I5-10210U",
                        "3500U", "I7-1165G7", "4700U",
                        "I7-10510U", "5700U", "I7-1185G7", "I5-1340P"
                );
            default:
                return List.of();
        }
    }

//인터넷 사용 목적에 따라 cpu 태그를 반환
    private List<String> getInternetTag(String internet){

        if (internet == null) {
            return List.of();
        }

        switch (internet) {

            case "인터넷 강의 볼거에요":
                return List.of("i3-1115G4", "i3-1125G4", "i5-10210U", "i5-10300H", "i5-1135G7", "i5-11300H", "i5-11320H",
                        "i5-11400H", "i5-1155G7", "i5-12450H", "i5-12500H", "i7-10510U", "i7-10750H", "i7-1165G7", "i7-11370H", "i7-11390H",
                        "i7-11600H", "i7-1185G7", "i7-12650H", "i7-12700H", "i9-11900H", "i9-12900H",  "4300U", "5300U", "4500U", "4600H", "5500U",
                        "5600H", "5600U", "6600H", "6600U", "4700U", "4800H", "5700U", "5800H", "5800U", "6800H", "6800U", "5900HX", "5980HS", "6900HX");//cpu 중간

            case "문서 작업 할거에요": //cpu 최하위군 펜티엄
                return List.of("i3-10110U", "i3-1005G1", "i3-1115G4", "i3-1125G4", "i3-1215U", "i5-10210U",
                        "i5-1035G1", "i5-1135G7", "i5-1155G7", "i5-1235U", "i5-1240P", "i5-11300H", "i5-11320H", "i5-12450H",
                        "i5-1340P", "i7-10510U", "i7-1065G7", "i7-1165G7", "i7-1185G7", "i7-1195G7", "i7-1255U", "i7-1260P", "i7-11370H", "i7-11390H",
                        "i7-12650H", "Pentium Gold 6405U", "Pentium Gold 7505", "Pentium Gold 7505T", "Pentium Gold 7505U", "Pentium Silver N5030","Celeron N4020",
                        "Celeron N4500", "Celeron N5100");

            default:
                return List.of();
        }
    }
//장소에 따라 무게 태그를 반환
    private List<String> getPlace(String place) {

        if (place == null) {//장소가 null이면 빈 리스트 반환
            return List.of(); //빈 리스트 반환
        }
        switch (place) {
            case "가지고 다닐거에요":
                return List.of("1.1kg", "1.12kg", "1.14kg", "1.18", "1.199kg", "1.21kg", "1.25kg", "1.299kg", "1.3kg", "1.36kg", "1.39kg", "1.35kg", "1.4kg", "1.45kg",
                        "1.5kg", "1.55kg", "1.57kg", "1.6kg", "1.65kg", "1.7kg", "1.74kg", "1.75kg", "1.8kg", "1.85kg", "1.9kg", "1.95kg", "2kg");

            case "집에서 사용할거에요":
                return List.of("2.05kg", "2.08kg", "2.1kg", "2.15kg", "2.2kg", "2.25kg",
                        "2.3kg", "2.35kg","2.38kg", "2.4kg", "2.45kg", "2.5kg", "2.55kg", "2.6kg", "2.65kg",
                        "2.7kg", "2.75kg", "2.8kg", "2.85kg", "2.9kg", "2.95kg","2.99kg", "3kg");
            default:
                return List.of();
        }


    }

//성능에 따라 가격 범위를 반환
    private int[] getPriceRange(String performance) {
        if (performance == null) {
            return new int[]{0, Integer.MAX_VALUE};
        }
        switch (performance) { //성능에 따라 가격 범위 반환
            case "성능용":
                return new int[]{1500000, Integer.MAX_VALUE};  // 150만원 이상
            case "타협":
                return new int[]{700000, 1500000};  // 70만원 ~ 150만원
            case "밸런스용":
                return new int[]{1000000, 2000000};  // 100만원 ~ 200만원
            default:
                return new int[]{0, Integer.MAX_VALUE};
        }
        }

//    private int[] getGameperformance(String gameperformance) {
//        if (gameperformance == null) {
//            return new int[]{0, Integer.MAX_VALUE};
//        }
//        switch (gameperformance) {
//            case "성능용":
//                return new int[]{2000000, Integer.MAX_VALUE};  // 150만원 이상
//            case "타협":
//                return new int[]{1000000, 1400000};  // 70만원 ~ 150만원
//            case "밸런스용":
//                return new int[]{1500000, 2500000};  // 150만원 ~ 250만원
//            default:
//                return new int[]{0, Integer.MAX_VALUE};
//        }
//    }

    //화면 크기에 따라 태그를 반환
        private List<String> getScreenSizeTags(String screen) {

            if (screen == null) {
                return List.of();
            }

            switch (screen) {
                case "화면 넓은게 좋아요":
                    return List.of("(18인치)", "(17인치)", "(17.3인치)");

                case "적당한게 좋아요":
                    return List.of("(15.6인치)", "(16인치)", "(15인치)");

                case "작은 화면이 좋아요":
                    return List.of("(13.3인치)", "(14인치)");
                default:
                    return List.of();
            }


        }
//    private List<String> getBatteryTag(String batteryTag) {
//        if (batteryTag == null) {
//            return List.of();
//        }
//
//        // "WH" 제거 및 소문자 "wh" 고려
//        String numericPart = batteryTag.replaceAll("(?i)Wh$", "").trim();
//
//        try {
//            double batteryCapacity = Double.parseDouble(numericPart);
//
//            if (batteryCapacity >= 70.0 && batteryCapacity <= 99.9) {
//                return List.of("70Wh~99.9Wh");
//            } else if (batteryCapacity >= 30.0 && batteryCapacity <= 68.0) {
//                return List.of("30Wh~68Wh");
//            } else {
//                return List.of();
//            }
//        } catch (NumberFormatException e) {
//            // 숫자로 변환할 수 없는 경우 처리
//            return List.of();
        }
//    }
//    }
//}
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

//}