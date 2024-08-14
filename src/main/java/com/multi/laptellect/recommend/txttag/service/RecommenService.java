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



        String gpuName = laptopSpecDTO.getGpu().getGpuChipset();
        String screenSize = laptopSpecDTO.getDisplay().getScreenSize();
        String osName = laptopSpecDTO.getOs();
        String thicName = laptopSpecDTO.getPortability().getThickness();
        String usbNo = laptopSpecDTO.getAddOn().getUsb();
        String recentNo = laptopSpecDTO.getRegistrationDate();
        String weightName = laptopSpecDTO.getPortability().getWeight();
        String powerName = laptopSpecDTO.getPower().getAdapter();
        String storageName = laptopSpecDTO.getStorage().getStorageCapacity();
        String cpuName = laptopSpecDTO.getCpu().getCpuNumber();


        //gpuName, screenSize 변수명 변경


        if (isGpuSuitableForSteamOrFPS(gpuName)) {
            tagNo = findTagByData(tags, "게이밍");
            assignedTags.add(tagNo);
            log.info("제품 {}에 '게이밍' 태그(#{}) 할당", tagNo);
        }
        if (isGpuSuitableForOnlineGames(gpuName, cpuName)) {
            tagNo = findTagByData(tags, "펠월드");
            assignedTags.add(tagNo);
            log.info(" '펠월드' 태그(#{}) 할당", tagNo);
        }
//        if (isGpuSuitableForAOSGames(gpuName)) {
//            tagNo = findTagByData(tags, "가성비");
//            assignedTags.add(tagNo);
//            log.info(" '가성비' 태그(#{}) 할당", tagNo);
//        }

        if (isScreenSuitableForCoding(screenSize)) {
            tagNo = findTagByData(tags, "넓은 화면");
            assignedTags.add(tagNo);
            log.info(" '넓은 화면' 태그(#{}) 할당", tagNo);
        }
        if (isScreenSuitableForDocuments(screenSize)) {
            tagNo = findTagByData(tags, "작은 화면");
            assignedTags.add(tagNo);
            log.info(" '작은 화면' 태그(#{}) 할당", tagNo);
        }
        if (isScreenSuitableForStudents(screenSize)) {
            tagNo = findTagByData(tags, "적당한 화면");
            assignedTags.add(tagNo);
            log.info(" '적당한 화면' 태그(#{}) 할당", tagNo);
        }
        if (isWindowsOS(osName)) {
            tagNo = findTagByData(tags, "윈도우 있음");
            assignedTags.add(tagNo);
            log.info(" '윈도우' 태그(#{}) 할당", tagNo);
        }
        if (isSlim(thicName)) {
            tagNo = findTagByData(tags, "슬림");
            assignedTags.add(tagNo);
            log.info(" '슬림' 태그(#{}) 할당", tagNo);
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
        if (isRecent(recentNo)) {
            tagNo = findTagByData(tags, "최신제품");
            assignedTags.add(tagNo);
            log.info("'최신 제품' 태그(#{}) 할당", tagNo);
        }
        if (isWeight(weightName)) {
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
        if (isCoding(cpuName)) {
            tagNo = findTagByData(tags, "코딩");
            assignedTags.add(tagNo);
            log.info("'코딩용' 태그(#{}) 할당", tagNo);
        }
        if (isWork(cpuName)) {
            tagNo = findTagByData(tags, "작업용");
            assignedTags.add(tagNo);
            log.info("'작업용' 태그(#{}) 할당", tagNo);
        }
        if (isLol(cpuName)) {
            tagNo = findTagByData(tags, "리그오브레전드");
            assignedTags.add(tagNo);
            log.info("'리그오브레전드' 태그(#{}) 할당", tagNo);
        }
        if (isBet(cpuName, gpuName)) {
            tagNo = findTagByData(tags, "배틀그라운드");
            assignedTags.add(tagNo);
            log.info("'배틀 그라운드' 태그(#{}) 할당", tagNo);
        }
        if (islost(cpuName, gpuName)){
            tagNo = findTagByData(tags, "로스트 아크");
            assignedTags.add(tagNo);
            log.info("'로스트 아크' 태그(#{}) 할당", tagNo);
        }


        return assignedTags;
    }

    private boolean islost(String cpu, String gpu) {
        Map<String, Integer> cEnt = cpuConfig.getCpuMark();
        Map<String, Integer> gEnt = gpuConfig.getGpuMark();
        Pattern pattern2 = Pattern.compile("[\\s()]+");
        String Key3 = pattern2.matcher("i3-6100U (2.3GHz)").replaceAll("");
        String Key4 = pattern2.matcher("GTX1050 Ti").replaceAll("");
        Integer cpuScore = cEnt.get(Key3);
        Integer gpuScore = gEnt.get(Key4);

        if (cpu == null || gpu == null || cpuScore == null || gpuScore == null) {
            return false;
        }

        for (String cpuName : cEnt.keySet()) {
            String cpuNameC1 = pattern2.matcher(cpuName).replaceAll("");
            if (cpu.replaceAll("[\\s()]+", "").contains(cpuNameC1)) {
                int cpuCode = cEnt.get(cpuName);

                for (String gpuName : gEnt.keySet()) {
                    String gpuNameClean = pattern2.matcher(gpuName).replaceAll("");
                    if (gpu.replaceAll("[\\s()]+", "").contains(gpuNameClean)) {
                        int gpuCode = gEnt.get(gpuName);
                        return cpuCode >= cpuScore && gpuCode >= gpuScore;
                    }
                }
            }
        }
        return false;
    }

    private boolean isBet(String cpu, String gpu) {
        Map<String, Integer> cEnt = cpuConfig.getCpuMark();
        Map<String, Integer> gEnt = gpuConfig.getGpuMark();
        Pattern pattern2 = Pattern.compile("[\\s()]+");
        String Key3 = pattern2.matcher("i5-7300U (2.6GHz)").replaceAll("");
        String Key4 = pattern2.matcher("GTX1050 Ti").replaceAll("");
        Integer cpuScore = cEnt.get(Key3);
        Integer gpuScore = gEnt.get(Key4);

        if (cpu == null || gpu == null || cpuScore == null || gpuScore == null) {
            return false;
        }

        for (String cpuName : cEnt.keySet()) {
            String cpuNameC1 = pattern2.matcher(cpuName).replaceAll("");
            if (cpu.replaceAll("[\\s()]+", "").contains(cpuNameC1)) {
                int cpuCode = cEnt.get(cpuName);

                for (String gpuName : gEnt.keySet()) {
                    String gpuNameClean = pattern2.matcher(gpuName).replaceAll("");
                    if (gpu.replaceAll("[\\s()]+", "").contains(gpuNameClean)) {
                        int gpuCode = gEnt.get(gpuName);
                        return cpuCode >= cpuScore && gpuCode >= gpuScore;
                    }
                }
            }
        }
        return false;
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
                log.info("펠 cpu 소스 {} :", cpuScore1);
                return cpuCode > cpuScore1;
            }
        }
        return false;
    }

    private boolean isWork(String gpu) {
        Map<String, Integer> ent5 = cpuConfig.getCpuMark();
        Pattern pattern3 = Pattern.compile("[\\s()]+");
        String key4 = pattern3.matcher( "i7-1165G7 (2.8GHz)").replaceAll("");
        String key5 = pattern3.matcher("i3-1005G1 (1.2GHz)").replaceAll("");
        Integer cpuScoer5 = ent5.get(key4);
        Integer cpuScoer6 = ent5.get(key5);

        if (gpu == null) {
            return false;
        }
        if (cpuScoer5 == null || cpuScoer6 == null) {
            return false;
        }
            for (String cpuName2 : ent5.keySet()) {
                String cpuNameC2 = pattern3.matcher(cpuName2).replaceAll("");
                if (gpu.replaceAll("[\\s()]+", "").contains(cpuNameC2)) {
                    int cpuCode2 = ent5.get(cpuName2);
                    return cpuCode2 < cpuScoer5 && cpuCode2 > cpuScoer6;
                }
            }
            return false;
        }



    private boolean isCoding(String coding) {
        Map<String, Integer> ent3 = cpuConfig.getCpuMark();
        Pattern pattern = Pattern.compile("[\\s()]+");
        String key = pattern.matcher("i3-1315U (1.2GHz)").replaceAll("");
        Integer cpuScore = ent3.get(key);

        log.info("cpu소스 가져오나요? {} :", cpuScore);
        log.info("결과는요? {} :", coding);
        log.info("ent비어있나요? {}:", ent3);

        if (coding == null) {
            return false;
        }
        if (cpuScore == null)
            return false;
        for (String cpuName : ent3.keySet()) {
            String cpuNameC = pattern.matcher(cpuName).replaceAll("");
            if (coding.replaceAll("[\\s()]+", "").contains(cpuNameC)) {
                int cpuCode = ent3.get(cpuName);
                return cpuCode > cpuScore;
            }
        }
        return false;
    }

    private boolean isGpuSuitableForSteamOrFPS(String gpu) {
        Map<String, Integer> ent = gpuConfig.getGpuMark();
        Pattern pattern1 = Pattern.compile("[\\s()]+");
        String key2 = pattern1.matcher("GTX1650").replaceAll("");
        Integer gtxScore = ent.get(key2);

        log.info("gpu 소스 가져오나요 {} :", gtxScore);

        if (gpu == null) {
            return false;
        }
        if (gtxScore == null)
            return false;
        for (String gpuName : ent.keySet()) {
            String gpuNameG = pattern1.matcher(gpuName).replaceAll("");
            if (gpu.replaceAll("[\\s()]+", "").contains(gpuNameG)) {
                int gpuCode = ent.get(gpuName);
                return gpuCode >= gtxScore;
            }
        }
            return false;
    }
    private boolean isGpuSuitableForOnlineGames(String cpu, String gpu) {
        Map<String, Integer> cEnt = cpuConfig.getCpuMark();
        Map<String, Integer> gEnt = gpuConfig.getGpuMark();
        Pattern pattern2 = Pattern.compile("[\\s()]+");
        String Key3 = pattern2.matcher("i7-10750H (2.6GHz)").replaceAll("");
        String Key4 = pattern2.matcher("RTX4060").replaceAll("");
        Integer cpuScore1 = cEnt.get(Key3);
        Integer gpuScore1 = gEnt.get(Key4);
        log.info("펠 cpu 소스 {} :", cpuScore1);
        log.info("펠 gpu 소스 {} :", gpuScore1);

        if (cpu == null || gpu == null || cpuScore1 == null || gpuScore1 == null) {
            return false;
        }

        for (String cpuName : cEnt.keySet()) {
            String cpuNameC1 = pattern2.matcher(cpuName).replaceAll("");
            if (cpu.replaceAll("[\\s()]+", "").contains(cpuNameC1)) {
                int cpuCode = cEnt.get(cpuName);

                for (String gpuName : gEnt.keySet()) {
                    String gpuNameClean = pattern2.matcher(gpuName).replaceAll("");
                    if (gpu.replaceAll("[\\s()]+", "").contains(gpuNameClean)) {
                        int gpuCode = gEnt.get(gpuName);
                        log.info("펠 cpu 소스 {} :", cpuScore1);
                        log.info("펠 gpu 소스 {} :", gpuScore1);
                        return cpuCode >= cpuScore1 && gpuCode >= gpuScore1;

                    }
                }
            }
        }
        return false;
    }

//    private boolean isGpuSuitableForAOSGames(String gpu) {
//        Map<String, Integer> ent4 = gpuConfig.getGpuMark();
//        Pattern pattern2 = Pattern.compile("[\\s()]+");
//        String key2 = pattern2.matcher("");
//        if (gpu == null) {
//            return false;
//        }
//
//        );
//        for (String suitableGpu : suitableGpus) {
//            if (gpu.contains(suitableGpu)) {
//                return true;
//            }
//        }
//        return false;
//    }

    private boolean isScreenSuitableForCoding(String screena) {
        if (screena == null) {
            return false;
        } try {
            String point = screena.replaceAll("[^0-9.]", "");
            double screenaa = Double.parseDouble(point);
            return screenaa >= 16;
        } catch (Exception e){
            return false;
        }
    }

    private boolean isScreenSuitableForDocuments(String screenb) {
        if (screenb == null) {
            return false;
        } try {
            String point2 = screenb.replaceAll("[^0-9.]", "");
            double screenbb = Double.parseDouble(point2);
            return screenbb >= 13 && screenbb < 16;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isScreenSuitableForStudents(String screenc) {
        if (screenc == null) {
            return false;
        } try {
            String point3 = screenc.replaceAll("[^0-9.]", "");
            double screencc = Double.parseDouble(point3);
            return screencc < 13;
        } catch (Exception e) {
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
                log.info("윈도우 태그 할당 : {} {} ", os, suitableOss);
                return true;
            }
        }
        return false;
    }

    private boolean isSlim(String thickness) {
        if (thickness == null) {
            return false;
        } try {
            double thicknesValuee = Double.parseDouble(thickness.replace("mm", ""));
            return thicknesValuee < 20.0;
        } catch (Exception e){
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
                log.info("usb 많음 태그 할당 : {} {} ", usb, suitabletUsbs);
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
                log.info("usb 적음 태그 할당 : {} {} ", usbe, suitableUsebs);
                return true;
            }
        }
        return false;
    }

    private boolean isRecent(String recent) {
        if (recent == null) {
            return false;
        }
        List<String> suitableRecent = List.of("2024");
        for (String suitableRecents : suitableRecent) {
            if (recent.contains(suitableRecents)) {
                return true;
            }
        }
        return false;
    }

    private boolean isWeight(String weight) {
        if (weight == null) {
            return false;
        } try {
            double weighta = Double.parseDouble(weight.replace("kg", ""));
            return weighta < 2.1;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isWeighte(String weighte) {
        if (weighte == null){
            return false;
    } try {
            double weightee = Double.parseDouble(weighte.replace("kg", ""));
            return weightee > 2.1;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isPower(String power) {
        if (power == null) {
        } try {
            double powerWh = Double.parseDouble(power.replace("W", ""));
            return powerWh > 80;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isPowerSmall(String powersmall) {
        if (powersmall == null) {
        } try {
            double powerWhSmall = Double.parseDouble(powersmall.replace("W", ""));
            return powerWhSmall < 80;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isStorage(String storage) {
        if (storage == null) {
        } try {
            double storageBig = Double.parseDouble(storage.replace("GB", ""));
            return storageBig > 511;
        } catch (Exception e) {
            return false;
        }
    }


    private int findTagByData(List<TaggDTO> tags, String tagData ) {
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