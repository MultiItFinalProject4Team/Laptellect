package com.multi.laptellect.recommend.laptop.service;

import com.multi.laptellect.product.model.dto.LaptopDetailsDTO;
import com.multi.laptellect.product.model.dto.laptop.LaptopSpecDTO;
import com.multi.laptellect.product.model.mapper.ProductMapper;
import com.multi.laptellect.product.service.ProductService;
import com.multi.laptellect.recommend.laptop.model.dao.RecommendProductDAO;
import com.multi.laptellect.recommend.laptop.model.dto.CurationDTO;
import com.multi.laptellect.recommend.laptop.model.dto.ProductFilterDTO;
import com.multi.laptellect.recommend.txttag.model.dto.TaggDTO;
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
    private final RecommendProductDAO recommendProductDAO;

    public ArrayList<LaptopSpecDTO> getRecommendations(CurationDTO curationDTO) {
        //surveyResults는 사용자가 선택한 설문 결과를 담고 있는 맵
        ProductFilterDTO productFilterDTO = createSearchCriteria(curationDTO);
        log.info("큐레이션 조건 분류 완료 = {} " + productFilterDTO);


        // 조건에 따라 Product_no를 반환
        ArrayList<Integer> productNos = recommendProductDAO.findLaptopDetailByFilter(productFilterDTO);
        log.info("추천 결과 맞는 제품 번호 = {}", productNos);        ArrayList<LaptopSpecDTO> laptop = new ArrayList<>(); // 사용자에게 추천할 노트북 리스트 // 노트북 스펙을 담음

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

    public List<TaggDTO> getTagsForProduct(int productNo) {
        return recommendProductDAO.getTagsForProduct(productNo);
    }
    public ArrayList<LaptopSpecDTO> getAllProducts(int productNo) {
        return recommendProductDAO.getAllProducts(productNo); //제품 태그 넣을 예정
    }

    private ProductFilterDTO createSearchCriteria(CurationDTO curationDTO) {
        ProductFilterDTO productFilterDTO = new ProductFilterDTO(); // 사용자가 원하는 조건이 담긴 DTO

        log.debug("큐레이션 조건 반환 시작 = {}", curationDTO);
        String mainOption = curationDTO.getMainOption(); // key 값이 게임, 사무용, 장소 등 구분 공통 요소

        if (mainOption.equals("게임 할거에요")) { //키 값이 게임일 시 Gpu 중심
            String gpu = curationDTO.getGpu(); // 게임 타입
            List<String> gpuValues = getGpuTags(gpu); // 게임 타입에 따라 gpu 태그 반환
            productFilterDTO.setGpu(gpuValues); //gpu태그 설정
        } else if (mainOption.equals("작업 할거에요")) { // key 값이 사무용일 시 cpu 중심
            String cpu = curationDTO.getCpu(); // 사용 목적 (코드 작업, AI 작업)
            List<String>cpuValues = getCpuTags(cpu);
            productFilterDTO.setCpu(cpuValues);
        }else if (mainOption.equals("문서나 인강 볼거에요")){
            String internet = curationDTO.getInternet();
            List<String> internetValues = getInternetTag(internet);
            productFilterDTO.setInternet(internetValues);
        }

        String weight = curationDTO.getWeight();
        List<String> weightTags = getWeightTags(weight);
        productFilterDTO.setWeightTags(weightTags);

        String performance = curationDTO.getPerformance();
        List<String> gamingTags = getGamingTags(performance);
        productFilterDTO.setGamingTags(gamingTags);

        String screen = curationDTO.getScreen();
        List<String> screenTags = getScreenTags(screen);
        productFilterDTO.setScreen(screenTags);

        String somoweight = curationDTO.getSomoweight();
        List<String> somoWeightt = getsomoWeight(somoweight);
        productFilterDTO.setSomoweightTags(somoWeightt);


//        String battery = curationDTO.getBattery();
//        List<String> batteryValues = getBatteryTag(battery);
//        productFilterDTO.setBattery(batteryValues);


//
//        criteria.put("batteryTag", getBatteryTag(surveyResults.get("priority")));
//        criteria.put("designTag", getDesignTag(surveyResults.get("priority")));
//        criteria.put("performanceTag", getPerformanceTag(surveyResults.get("performance")));
//
//        String gameperformance = curationDTO.getGameperformance();
//        int[] gamepriceRange = getGameperformance(gameperformance);
//        productFilterDTO.setMinGamePrice(gamepriceRange[0]);
//        productFilterDTO.setMaxGamePrice(gamepriceRange[1]);

        return productFilterDTO;
    }
    private List<String> getsomoWeight(String somoweight) {
        log.info("무게 태그 설정 시작. 무게: {}", somoweight);
        if (somoweight == null) {
            return List.of();
        }
        switch (somoweight) {
            case "경량화":
                return List.of("경량화");
            case "무거움":
                return List.of("무거움");
            default:
                return List.of();
        }
    }

    private List<String> getWeightTags(String weight) {
        log.info("무게 태그 설정 시작. 무게: {}", weight);
        if (weight == null) {
            return List.of();
        }
        switch (weight) {
            case "가지고 다닐거에요":
                return List.of("가벼움");
            case "집에만 둘거에요":
                return List.of("무거움");
            default:
                return List.of();
        }
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
                        "펠월드", "로스트 아크"
                );
            case "온라인 게임":
                return List.of(
                        "배틀그라운드"
                );
            case "AOS게임":
                return List.of(
                        "리그오브레전드", "게이밍"
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
                return List.of("작업용"
                );
            case "일반 사무용 작업할거에요": //테스트를 위해 임시 Intel Xeon 라인 및 엔디비아 Quadro 라인 추가 될 예정
                return List.of("인터넷 강의"
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


////성능에 따라 가격 범위를 반환
private List<String> getGamingTags(String gameperformance) {
    log.info("게이밍 성능/가격 태그 설정 시작. 선택된 성능: {}", gameperformance);
    if (gameperformance == null) {
        return List.of();
    }
    switch (gameperformance) {
        case "성능용":
            return List.of("사무용 고성능");

        case "타협":
            return List.of("사무용 착한 가격");
        case "밸런스용":
            return List.of("사무용 가성비");
        default:
            return List.of();
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
        private List<String> getScreenTags (String screen) {

            if (screen == null) {
                return List.of();
            }

            switch (screen) {
                case "화면 넓은게 좋아요":
                    return List.of("넓은 화면");

                case "적당한게 좋아요":
                    return List.of("적당한 화면");

                case "작은 화면이 좋아요":
                    return List.of("작은 화면");
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