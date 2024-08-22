package com.multi.laptellect.recommend.txttag.service;

import com.multi.laptellect.product.model.dto.LaptopDetailsDTO;
import com.multi.laptellect.product.model.dto.laptop.LaptopSpecDTO;
import com.multi.laptellect.product.model.mapper.ProductMapper;
import com.multi.laptellect.product.service.ProductService;
import com.multi.laptellect.recommend.txttag.config.CpuConfig;
import com.multi.laptellect.recommend.txttag.config.GpuConfig;
import com.multi.laptellect.recommend.txttag.model.dao.ProductTagDAO;
import com.multi.laptellect.recommend.txttag.model.dto.TaggDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@Service
@Slf4j
public class RecommenService {

    private final ProductMapper productMapper; // 제품 매퍼 추가
    private final ProductTagDAO tagMapper; // 태그 매퍼 추가
    private final ProductService productService; // 제품 서비스 추가
    private final CpuConfig cpuConfig;
    private final GpuConfig gpuConfig;


    public void assignTagsToProducts() {
        log.info("태그 할당 프로세스 시작");

        ArrayList<Integer> productNOs = tagMapper.findAllProductNo(); // 제품 번호 조회
        log.info("제품 번호 수 = {}", productNOs.size());

        for (int productNO : productNOs) {
            log.info("제품 {} 처리 시작", productNO);
            List<LaptopDetailsDTO> laptopDetails = productMapper.laptopProductDetails(productNO);
            // 제품 번호별로 태그 할당


            if (!laptopDetails.isEmpty()) {
                log.info("제품 {}의 상세 정보 존재", laptopDetails);
                LaptopSpecDTO laptops = productService.getLaptopSpec(productNO, laptopDetails);

                List<Integer> tags = determineTagsForProduct(laptops);
                log.info("제품 {}에 할당된 태그 = {}", productNO, tags);

                for (int tag : tags) {
                    tagMapper.insertProductTag(productNO, tag);
                }


                log.info("태그 할당 완료 = {} ", productNO);

            } else {
                log.warn("상품 번호 {}에 대한 상세 정보가 없습니다.", productNO);
            }
        }
        log.info("태그 할당 프로세스 완료");
    }


    //제품번호별로 태그 할당
    private List<Integer> determineTagsForProduct(LaptopSpecDTO laptopSpecDTO) {
        List<Integer> assignedTags = new ArrayList<>(); //미리 선언 해두는게 나음
        List<TaggDTO> tags = tagMapper.findAllTag();
        int tagNo; //int 도 미리미리 해두는게 나음
        log.info("DTO 확인 : {}", laptopSpecDTO);


        String gpuTypeName = laptopSpecDTO.getGpu().getGpuType();//gpu 종류
        String gpuName = laptopSpecDTO.getGpu().getGpuChipset();//gpu
        String screenSize = laptopSpecDTO.getDisplay().getScreenSize();//화면 크기
        String osName = laptopSpecDTO.getOs();//운영 체제 여부
        String thicName = laptopSpecDTO.getPortability().getThickness();//두께
        String usbNo = laptopSpecDTO.getAddOn().getUsb();//usb 단자 갯수
        String recentNo = laptopSpecDTO.getRegistrationDate();//등록일
        String weightName = laptopSpecDTO.getPortability().getWeight();//무게
        String powerName = laptopSpecDTO.getPower().getAdapter();//어댑터
        String storageName = laptopSpecDTO.getStorage().getStorageCapacity();//저장공간
        String cpuName = laptopSpecDTO.getCpu().getCpuNumber(); //cpu
        String panelSurName = laptopSpecDTO.getDisplay().getPanelSurface(); //패널 표면 처리
        String refreshName = laptopSpecDTO.getDisplay().getResolution(); //해상도
        String priceName = laptopSpecDTO.getPrice(); //가격


        //gpuName, screenSize 변수명 변경
        if (isGpuSuitableForSteamOrFPS(gpuName, gpuTypeName)) {
            tagNo = findTagByData(tags, "게이밍");
            assignedTags.add(tagNo);
            log.info("'게이밍' 태그(#{}) 할당", tagNo);
        }
        if (isGpuSuitableForOnlineGames(gpuName, cpuName, gpuTypeName)) {
            tagNo = findTagByData(tags, "펠월드");
            assignedTags.add(tagNo);
            log.info("'펠월드' 태그(#{}) 할당", tagNo);
        }
        if (isScreenSuitableForCoding(screenSize)) {
            tagNo = findTagByData(tags, "넓은 화면");
            assignedTags.add(tagNo);
            log.info("'넓은 화면' 태그(#{}) 할당", tagNo);
        }
        if (isScreenSuitableForDocuments(screenSize)) {
            tagNo = findTagByData(tags, "중간 화면 사이즈");
            assignedTags.add(tagNo);
            log.info("'중간 화면 사이즈' 태그(#{}) 할당", tagNo);
        }
        if (isScreenSuitableForStudents(screenSize)) {
            tagNo = findTagByData(tags, "작은 화면");
            assignedTags.add(tagNo);
            log.info("'작은 화면' 태그(#{}) 할당", tagNo);
        }
        if (isWindowsOS(osName)) {
            tagNo = findTagByData(tags, "윈도우 있음");
            assignedTags.add(tagNo);
            log.info("'윈도우' 태그(#{}) 할당", tagNo);
        }
        if (isSlim(thicName)) {
            tagNo = findTagByData(tags, "슬림");
            assignedTags.add(tagNo);
            log.info("'슬림' 태그(#{}) 할당", tagNo);

        }
        if (isUsb(usbNo)) {
            tagNo = findTagByData(tags, "많은 USB 단자");
            assignedTags.add(tagNo);
            log.info("'많은 usb' 태그(#{}) 할당", tagNo);
        }
        if (isUsbe(usbNo)) {
            tagNo = findTagByData(tags, "적은 USB 단자");
            assignedTags.add(tagNo);
            log.info("'적은 Usb' 태그(#{}) 할당", tagNo);
        }
//        if (isRecent(recentNo)) {
//            tagNo = findTagByData(tags, "최신제품");
//            assignedTags.add(tagNo);
//            log.info("'최신 제품' 태그(#{}) 할당", tagNo);
//        }
        if (isWeightSuitable(weightName)) {
            tagNo = findTagByData(tags, "가벼움");
            assignedTags.add(tagNo);
            log.info("'가벼움' 태그(#{}) 할당", tagNo);
        }
        if (isWeighte(weightName)) {
            tagNo = findTagByData(tags, "무거움");
            assignedTags.add(tagNo);
            log.info("'무거움' 태그(#{}) 할당", tagNo);
        }
        if (isPower(powerName)) {
            tagNo = findTagByData(tags, "고전력");
            assignedTags.add(tagNo);
            log.info("'고전력' 태그(#{}) 할당", tagNo);
        }
        if (isPowerSmall(powerName)) {
            tagNo = findTagByData(tags, "저전력");
            assignedTags.add(tagNo);
            log.info("'저전력' 태그(#{}) 할당", tagNo);
        }
        if (isStorage(storageName)) {
            tagNo = findTagByData(tags, "넉넉한 저장 공간");
            assignedTags.add(tagNo);
            log.info("'넉넉한 저장 공간' 태그(#{}) 할당", tagNo);
        }
        if (isCoding(cpuName, gpuTypeName)) {
            tagNo = findTagByData(tags, "코딩");
            assignedTags.add(tagNo);
            log.info("'코딩용' 태그(#{}) 할당", tagNo);
        }
        if (isWork(cpuName, gpuTypeName)) {
            tagNo = findTagByData(tags, "작업용");
            assignedTags.add(tagNo);
            log.info("'작업용' 태그(#{}) 할당", tagNo);
        }
        if (isLol(cpuName)) {
            tagNo = findTagByData(tags, "리그오브레전드");
            assignedTags.add(tagNo);
            log.info("'리그오브레전드' 태그(#{}) 할당", tagNo);
        }
        if (isBet(cpuName, gpuName, gpuTypeName)) {
            tagNo = findTagByData(tags, "배틀그라운드");
            assignedTags.add(tagNo);
            log.info("'배틀 그라운드' 태그(#{}) 할당", tagNo);
        }
        if (islost(cpuName, gpuName, gpuTypeName)) {
            tagNo = findTagByData(tags, "로스트 아크");
            assignedTags.add(tagNo);
            log.info("'로스트 아크' 태그(#{}) 할당", tagNo);
        }
        if (ispaneSur(panelSurName)) {
            tagNo = findTagByData(tags, "눈부심 방지");
            assignedTags.add(tagNo);
            log.info("'눈부심 방지' 태그(#{}) 할당", tagNo);
        }
        if (isResolution(refreshName)) {
            tagNo = findTagByData(tags, "높은 해상도");
            assignedTags.add(tagNo);
            log.info("'높은 해상도' 태그(#{}) 할당", tagNo);
        }
        if (isPrice(priceName, gpuName)) {
            tagNo = findTagByData(tags, "착한 가격 겜트북");
            assignedTags.add(tagNo);
            log.info("'게이밍 착한 가격' 태그(#{}) 할당", tagNo);
        }
        if (isPriceEff(priceName, gpuName)) {
            tagNo = findTagByData(tags, "가성비 겜트북");
            assignedTags.add(tagNo);
            log.info("'게이밍 가성비' 태그(#{}) 할당", tagNo);
        }
        if (isPriceSmo(priceName, cpuName)) {
            tagNo = findTagByData(tags, "착한 가격");
            assignedTags.add(tagNo);
            log.info("'사무용 착한 가격' 태그(#{}) 할당", tagNo);
        }
        if (isPriceSmoGod(priceName, gpuTypeName)) {
            tagNo = findTagByData(tags, "가성비");
            assignedTags.add(tagNo);
            log.info("'사무용 가성비' 태그(#{}) 할당", tagNo);
        }
        if (isPriceSmoGo(priceName, gpuTypeName)) {
            tagNo = findTagByData(tags, "비즈니스 모델");
            assignedTags.add(tagNo);
            log.info("'사무용 고성능' 태그(#{}) 할당", tagNo);

        }
        if (isPriceGo(priceName, gpuName)) {
            tagNo = findTagByData(tags, "고성능");
            assignedTags.add(tagNo);
            log.info("'게이밍 고성능' 태그(#{}) 할당", tagNo);
        }
        if (isInten(cpuName, gpuTypeName)) {
            tagNo = findTagByData(tags, "사무용");
            assignedTags.add(tagNo);
            log.info("'인터넷 강의' 태그(#{}) 할당", tagNo);
        }
        if (isSomoWeight(weightName)) {
            tagNo = findTagByData(tags, "초경량");
            assignedTags.add(tagNo);
            log.info("'초경량' 태그(#{}) 할당", tagNo);
        }

        return assignedTags;
    }


    private boolean islost(String cpu, String gpu, String gpuTypeName) {
        Map<String, Integer> cEnt = cpuConfig.getCpuMark();
        Map<String, Integer> gEnt = gpuConfig.getGpuMark();
        Pattern pattern = Pattern.compile("[\\s()]+");
        String minCpuKey = pattern.matcher("i3-6100U (2.3GHz)").replaceAll("");
        String minGpuKey = pattern.matcher("GTX1050 Ti").replaceAll("");
        Integer minCpuScore = cEnt.get(minCpuKey);
        Integer minGpuScore = gEnt.get(minGpuKey);
        int cpuScore = -1;
        int gpuScore = -1;

        if (cpu == null || gpu == null || minCpuScore == null || minGpuScore == null || gpuTypeName == null || gpuTypeName.equals("내장그래픽")) {
            return false;
        }

        for (String cpuName : cEnt.keySet()) {
            String cpuNameC1 = pattern.matcher(cpuName).replaceAll("");
            if (cpu.replaceAll("[\\s()]+", "").contains(cpuNameC1)) {
                cpuScore = cEnt.get(cpuName);
                break;
            }
        }
        for (String gpuName : gEnt.keySet()) {
            String gpuNameClean = pattern.matcher(gpuName).replaceAll("");
            if (gpu.replaceAll("[\\s()]+", "").contains(gpuNameClean)) {
                gpuScore = gEnt.get(gpuName);
                break;
            }
        }
        return cpuScore >= minCpuScore && gpuScore >= minGpuScore;
    }

    private boolean isBet(String cpu, String gpu, String gpuTypeName) {
        Map<String, Integer> cEnt = cpuConfig.getCpuMark();
        Map<String, Integer> gEnt = gpuConfig.getGpuMark();
        Pattern pattern2 = Pattern.compile("[\\s()]+");
        String Key3 = pattern2.matcher("i5-8200Y (1.3GHz)").replaceAll("");
        String Key4 = pattern2.matcher("GTX1050 Ti").replaceAll("");
        Integer cpuScore = cEnt.get(Key3);
        Integer gpuScore = gEnt.get(Key4);
        int cpuCode = -1;
        int gpuCode = -1;

        if (cpu == null || gpu == null || cpuScore == null || gpuScore == null || gpuTypeName == null || gpuTypeName.equals("내장그래픽")) {
            return false;
        }

        for (String cpuName : cEnt.keySet()) {
            String cpuNameC1 = pattern2.matcher(cpuName).replaceAll("");
            if (cpu.replaceAll("[\\s()]+", "").contains(cpuNameC1)) {
                cpuCode = cEnt.get(cpuName);
                break;
            }
        }

        for (String gpuName : gEnt.keySet()) {
            String gpuNameClean = pattern2.matcher(gpuName).replaceAll("");
            if (gpu.replaceAll("[\\s()]+", "").contains(gpuNameClean)) {
                gpuCode = gEnt.get(gpuName);
                break;
            }
        }
        return cpuCode >= cpuScore && gpuCode >= gpuScore;
    }


    private boolean isLol(String cpu) {
        Map<String, Integer> cEnt = cpuConfig.getCpuMark();
        Pattern pattern = Pattern.compile("[\\s()]+");
        String Key1 = pattern.matcher("i5-8265U (1.6GHz)").replaceAll("");
        Integer cpuScore1 = cEnt.get(Key1);

        if (cpu == null || cpuScore1 == null) {
            return false;
        }

        for (String cpuName : cEnt.keySet()) {
            String cpuNameC1 = pattern.matcher(cpuName).replaceAll("");
            if (cpu.replaceAll("[\\s()]+", "").contains(cpuNameC1)) {
                int cpuCode = cEnt.get(cpuName);
                return cpuCode > cpuScore1;
            }
        }
        return false;
    }

    private boolean isWork(String cpu, String gpuTypeName) {
        Map<String, Integer> ent5 = cpuConfig.getCpuMark();
        Pattern pattern3 = Pattern.compile("[\\s()]+");
        String key4 = pattern3.matcher("i5-1130G7 (1.8GHz)").replaceAll("");
        String key5 = pattern3.matcher("m3-6Y30 (0.9GHz)").replaceAll("");
        Integer cpuScoer5 = ent5.get(key4);
        Integer cpuScoer6 = ent5.get(key5);

        if (cpu == null || gpuTypeName == null) {
            return false;
        }
        if (cpuScoer5 == null || cpuScoer6 == null) {
            return false;
        }

        // 내장 그래픽 카드 여부 확인
        boolean isIntegratedGraphics = gpuTypeName.equals("내장그래픽");

        for (String cpuName2 : ent5.keySet()) {
            String cpuNameC2 = pattern3.matcher(cpuName2).replaceAll("");
            if (cpu.replaceAll("[\\s()]+", "").contains(cpuNameC2)) {
                int cpuCode2 = ent5.get(cpuName2);
                // CPU 성능 조건과 내장 그래픽 조건을 모두 만족해야 함
                return cpuCode2 < cpuScoer5 && cpuCode2 > cpuScoer6 && isIntegratedGraphics;
            }
        }
        return false;
    }

    private boolean isCoding(String cpu, String gpuTypeName) {
        Map<String, Integer> ent5 = cpuConfig.getCpuMark();
        Pattern pattern3 = Pattern.compile("[\\s()]+");
        String key4 = pattern3.matcher("i7-1165G7 (2.8GHz)").replaceAll("");
        String key5 = pattern3.matcher("m3-6Y30 (0.9GHz)").replaceAll("");

        Integer cpuScoer5 = ent5.get(key4);
        Integer cpuScoer6 = ent5.get(key5);

        if (cpu == null || gpuTypeName == null) {
            return false;
        }
        if (cpuScoer5 == null || cpuScoer6 == null) {
            return false;
        }

        // 내장 그래픽 카드 여부 확인
        boolean isIntegratedGraphics = gpuTypeName.equals("내장그래픽");

        for (String cpuName2 : ent5.keySet()) {
            String cpuNameC2 = pattern3.matcher(cpuName2).replaceAll("");
            if (cpu.replaceAll("[\\s()]+", "").contains(cpuNameC2)) {
                int cpuCode2 = ent5.get(cpuName2);
                // CPU 성능 조건과 내장 그래픽 조건을 모두 만족해야 함
                return cpuCode2 < cpuScoer5 && cpuCode2 > cpuScoer6 && isIntegratedGraphics;
            }
        }
        return false;
    }


    private boolean isGpuSuitableForSteamOrFPS(String gpu, String gpuTypeName) {
        Map<String, Integer> ent = gpuConfig.getGpuMark();
        Pattern pattern1 = Pattern.compile("[\\s()]+");
        String key2 = pattern1.matcher("GTX1050 Ti").replaceAll("");
        Integer gtxScore = ent.get(key2);

        if (gpuTypeName == null || gpuTypeName.equals("내장그래픽")) {
            return false;
        }
        int gpuCode = -1;

        if (gpu == null) {
            return false;
        }
        if (gtxScore == null)
            return false;

        for (String gpuName : ent.keySet()) {
            String gpuNameG = pattern1.matcher(gpuName).replaceAll("");
            if (gpu.replaceAll("[\\s()]+", "").contains(gpuNameG)) {
                gpuCode = ent.get(gpuName);
                break;
            }
        }
        return gpuCode >= gtxScore;
    }

    private boolean isGpuSuitableForOnlineGames(String cpu, String gpu, String gpuTypeName) {
        Map<String, Integer> cEnt = cpuConfig.getCpuMark();
        Map<String, Integer> gEnt = gpuConfig.getGpuMark();
        Pattern pattern2 = Pattern.compile("[\\s()]+");
        String Key3 = pattern2.matcher("i7-10750H (2.6GHz)").replaceAll("");
        String Key4 = pattern2.matcher("RTX4060").replaceAll("");
        Integer cpuScore1 = cEnt.get(Key3);
        Integer gpuScore1 = gEnt.get(Key4);
        int cpuCode = -1;
        int gpuCode = -1;

        if (cpu == null || gpu == null || cpuScore1 == null || gpuScore1 == null || gpuTypeName == null || gpuTypeName.equals("내장그래픽")) {
            return false;
        }

        for (String cpuName : cEnt.keySet()) {
            String cpuNameC1 = pattern2.matcher(cpuName).replaceAll("");
            if (cpu.replaceAll("[\\s()]+", "").contains(cpuNameC1)) {
                cpuCode = cEnt.get(cpuName);
                break;
            }
        }
        for (String gpuName : gEnt.keySet()) {
            String gpuNameClean = pattern2.matcher(gpuName).replaceAll("");
            if (gpu.replaceAll("[\\s()]+", "").contains(gpuNameClean)) {
                gpuCode = gEnt.get(gpuName);
                break;
            }
        }
        return cpuCode >= cpuScore1 && gpuCode >= gpuScore1;
    }

    private boolean isScreenSuitableForCoding(String screenSize) {
        if (screenSize == null || screenSize.isEmpty()) {
            return false;
        }
        try {
            // 괄호 안의 인치 값을 추출
            int startIndex = screenSize.lastIndexOf("(");
            int endIndex = screenSize.lastIndexOf("인치)");
            if (startIndex == -1 || endIndex == -1) {
                return false;
            }
            String inchesStr = screenSize.substring(startIndex + 1, endIndex);
            double inches = Double.parseDouble(inchesStr);

            // 16인치 이상인지 확인
            return inches >= 16.0;
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            return false;
        }
    }

    private boolean isScreenSuitableForDocuments(String screenSize) {
        // 1. 입력값 검증
        if (screenSize == null || screenSize.isEmpty()) {
            return false;
        }
        try {
            // 2. 인치 값 추출
            int startIndex = screenSize.lastIndexOf("(");
            int endIndex = screenSize.lastIndexOf("인치)");
            if (startIndex == -1 || endIndex == -1) {
                return false;
            }
            String inchesStr = screenSize.substring(startIndex + 1, endIndex);

            // 3. 문자열을 숫자로 변환
            double inches = Double.parseDouble(inchesStr);

            // 4. 조건 확인
            return inches > 13.0 && inches < 16.0;
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            return false;
        }
    }

    private boolean isScreenSuitableForStudents(String screenSize) {
        if (screenSize == null || screenSize.isEmpty()) {
            return false;
        }
        try {
            // 괄호 안의 인치 값을 추출
            int startIndex = screenSize.lastIndexOf("(");
            int endIndex = screenSize.lastIndexOf("인치)");
            if (startIndex == -1 || endIndex == -1) {
                return false;
            }
            String inchesStr = screenSize.substring(startIndex + 1, endIndex);
            double inches = Double.parseDouble(inchesStr);

            // 13인치 미만인지 확인
            return inches < 13.0;
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            return false;
        }
    }

    private boolean isWindowsOS(String os) {
        if (os == null) {
            return false;
        }
        List<String> suitableOs = List.of("윈도우11프로", "윈도우11홈", "윈도우10 프로", "윈도우11(설치)", "윈도우10(설치)", "윈도우10");
        for (String suitableOss : suitableOs) {
            if (os.contains(suitableOss)) {
                return true;
            }
        }
        return false;
    }

    private boolean isSlim(String thickness) {
        if (thickness == null) {
            return false;
        }
        try {
            double thicknesValuee = Double.parseDouble(thickness.replace("mm", ""));
            return thicknesValuee < 20.0;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isUsb(String usb) {
        if (usb == null) {
            return false;
        }
        List<String> suitableteUsb = List.of("총5개", "총6개", "총4개");
        for (String suitabletUsbs : suitableteUsb) {
            if (usb.contains(suitabletUsbs)) {
                return true;
            }
        }
        return false;
    }

    private boolean isUsbe(String usbe) {
        if (usbe == null) {
            return false;
        }
        List<String> suitableUsbe = List.of("총3개", "총2개");
        for (String suitableUsebs : suitableUsbe) {
            if (usbe.contains(suitableUsebs)) {
                return true;
            }
        }
        return false;
    }

//    private boolean isRecent(String recent) {
//        if (recent == null) {
//            return false;
//        }
//        List<String> suitableRecent = List.of("2024");
//        for (String suitableRecents : suitableRecent) {
//            if (recent.contains(suitableRecents)) {
//                return true;
//            }
//        }
//        return false;
//    }

    private boolean ispaneSur(String panel) {
        if (panel == null) {
            return false;
        }
        List<String> suitableSur = List.of("눈부심 방지");
        for (String suitableSure : suitableSur) {
            if (panel.contains(suitableSure)) {

            }
        }
        return true;

    }

    private boolean isWeightSuitable(String weight) {
        if (weight == null || weight.isEmpty()) {
            return false;
        }
        try {
            // "kg" 앞의 숫자 부분만 추출
            String numericPart = weight.replaceAll("[^0-9.]", "");
            double weightInKg = Double.parseDouble(numericPart);

            // 2.1kg 미만인지 확인
            return weightInKg < 2.1;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isWeighte(String weighte) {
        if (weighte == null || weighte.isEmpty()) {
            return false;
        }
        try {
            // "kg" 앞의 숫자 부분만 추출
            String numericPart = weighte.replaceAll("[^0-9.]", "");
            double weightInKg = Double.parseDouble(numericPart);

            // 2.1kg 초과인지 확인
            return weightInKg > 2.1;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isSomoWeight(String weight) {
        if (weight == null || weight.isEmpty()) {
            return false;
        }
        try {
            // "kg" 앞의 숫자 부분만 추출
            String numericPart = weight.replaceAll("[^0-9.]", "");
            double weightInKg = Double.parseDouble(numericPart);

            // 1.5kg 미만인지 확인
            return weightInKg < 1.7;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isPower(String power) {
        if (power == null) {
        }
        try {
            double powerWh = Double.parseDouble(power.replace("W", ""));
            return powerWh > 80;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isPowerSmall(String powersmall) {
        if (powersmall == null) {
        }
        try {
            double powerWhSmall = Double.parseDouble(powersmall.replace("W", ""));
            return powerWhSmall < 80;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isStorage(String storage) {
        if (storage == null) {
        }
        try {
            double storageBig = Double.parseDouble(storage.replace("GB", ""));
            return storageBig > 511;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isResolution(String resolution) {
        if (resolution == null) {
        }
        try {
            double resolutionBig = Double.parseDouble(resolution.replaceAll("[\\\\s()a-zA-Z-]+", ""));
            return resolutionBig > 19201080;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isPrice(String price, String gpu) {
        if (price == null || gpu == null) {
            return false;
        }
        try {
            double priceValue = Double.parseDouble(price.replace(",", ""));
            boolean isPriceInRange = priceValue < 1000000;

            Map<String, Integer> gEnt = gpuConfig.getGpuMark();
            Pattern pattern = Pattern.compile("[\\s()]+");
            String gpuKey = pattern.matcher(gpu).replaceAll("");
            Integer gpuScore = gEnt.get(gpuKey);

            // GPU 성능이 RTX4060 이상인지 확인 (RTX4060의 점수는 17000)
            boolean isHighPerformanceGPU = gpuScore != null && gpuScore >= 8500;

            // 가격 조건과 GPU 성능 조건 모두 만족해야 함
            return isPriceInRange && isHighPerformanceGPU;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isPriceEff(String priceEff, String gpu) {
        if (priceEff == null || gpu == null) {
            return false;
        }
        try {
            double priceValueEff = Double.parseDouble(priceEff.replace(",", ""));
            boolean isPriceInRange = priceValueEff >= 1000000 && priceValueEff < 2600000;

            Map<String, Integer> gEnt = gpuConfig.getGpuMark();
            Pattern pattern = Pattern.compile("[\\s()]+");
            String gpuKey = pattern.matcher(gpu).replaceAll("");
            Integer gpuScore = gEnt.get(gpuKey);

            // GPU 성능이 RTX4060 이상인지 확인 (RTX4060의 점수는 17000)
            boolean isHighPerformanceGPU = gpuScore != null && gpuScore >= 12000;

            // 가격 조건과 GPU 성능 조건 모두 만족해야 함
            return isPriceInRange && isHighPerformanceGPU;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isPriceSmo(String priceSmo, String gpuTypeName) {
        if (priceSmo == null || gpuTypeName == null) {
            return false;
        }
        try {
            double priceValueSmo = Double.parseDouble(priceSmo.replaceAll(",", ""));
            boolean isPriceInRange = priceValueSmo < 1000000;

            // 내장 그래픽 카드 여부 확인
            boolean isIntegratedGraphics = gpuTypeName.equals("내장그래픽");

            // 가격 조건과 내장 그래픽 조건 모두 만족해야 함
            return isPriceInRange && isIntegratedGraphics;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isPriceSmoGo(String priceSmoGo, String gpuTypeName) {
        if (priceSmoGo == null || gpuTypeName == null) {
            return false;
        }
        try {
            double priceValueSmoGo = Double.parseDouble(priceSmoGo.replaceAll(",", ""));
            boolean isPriceInRange = priceValueSmoGo > 2000000;

            // 내장 그래픽 카드 여부 확인
            boolean isIntegratedGraphics = gpuTypeName.equals("내장그래픽");

            // 가격 조건과 내장 그래픽 조건 모두 만족해야 함
            return isPriceInRange && isIntegratedGraphics;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isInten(String inten, String gpuTypeName) {
        Map<String, Integer> ent5 = cpuConfig.getCpuMark();
        Pattern pattern3 = Pattern.compile("[\\s()]+");
        String cpukey = pattern3.matcher("i5-4200U (1.6GHz)").replaceAll("");
        Integer cpuScoer = ent5.get(cpukey);

        if (inten == null || gpuTypeName == null) {
            return false;
        }
        if (cpuScoer == null) {
            return false;
        }

        // 내장 그래픽 카드 여부 확인
        boolean isIntegratedGraphics = gpuTypeName.equals("내장그래픽");

        for (String cpuName : ent5.keySet()) {
            String cpuNameC = pattern3.matcher(cpuName).replaceAll("");
            if (inten.replaceAll("[\\s()]+", "").contains(cpuNameC)) {
                int cpuCode = ent5.get(cpuName);
                // CPU 성능이 기준점(cpuScoer)보다 낮으면서, 내장 그래픽일 경우
                if (cpuCode > cpuScoer && isIntegratedGraphics) {
                    return true;
                }
            }
        }
        return false;
    }


    private boolean isPriceSmoGod(String priceSmoGod, String gpuTypeName) {
        if (priceSmoGod == null || gpuTypeName == null) {
            return false;
        }
        try {
            double priceValueSmoGod = Double.parseDouble(priceSmoGod.replaceAll(",", ""));
            boolean isPriceInRange = priceValueSmoGod >= 1000000 && priceValueSmoGod < 1500000;

            // 내장 그래픽 카드 여부 확인
            boolean isIntegratedGraphics = gpuTypeName.equals("내장그래픽");

            //내장 가격 모두만족
            return isPriceInRange && isIntegratedGraphics;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isPriceGo(String priceGo, String gpu) {
        if (priceGo == null || gpu == null) {
            return false;
        }
        try {
            double priceValueGo = Double.parseDouble(priceGo.replace(",", ""));
            boolean isPriceInRange = priceValueGo >= 2300000;

            Map<String, Integer> gEnt = gpuConfig.getGpuMark();
            Pattern pattern = Pattern.compile("[\\s()]+");
            String gpuKey = pattern.matcher(gpu).replaceAll("");
            Integer gpuScore = gEnt.get(gpuKey);

            // GPU 성능이 RTX4060 이상인지 확인 (RTX4060의 점수는 17000)
            boolean isHighPerformanceGPU = gpuScore != null && gpuScore >= 17000;

            // 가격 조건과 GPU 성능 조건 모두 만족해야 함
            return isPriceInRange && isHighPerformanceGPU;
        } catch (Exception e) {
            return false;
        }
    }

    private int findTagByData(List<TaggDTO> tags, String tagData) {
        for (TaggDTO tag : tags) {
            if (tag.getTagData().equals(tagData)) {
                log.info("태그 '{}' 찾음: #{}", tagData, tag.getTagNo());
                return tag.getTagNo();
            }
        }
        log.warn("태그 '{}' 를 찾을 수 없음", tagData);
        return -1;
    }
}

