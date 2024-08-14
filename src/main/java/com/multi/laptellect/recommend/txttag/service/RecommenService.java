package com.multi.laptellect.recommend.txttag.service;

import com.multi.laptellect.product.model.dto.LaptopDetailsDTO;
import com.multi.laptellect.product.model.dto.laptop.LaptopSpecDTO;
import com.multi.laptellect.product.model.mapper.ProductMapper;
import com.multi.laptellect.product.service.ProductService;
import com.multi.laptellect.recommend.txttag.model.dao.ProductTagDAO;
import com.multi.laptellect.recommend.txttag.model.dto.TaggDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class RecommenService {

    private final ProductMapper productMapper; // 제품 매퍼 추가
    private final ProductTagDAO tagMapper; // 태그 매퍼 추가
    private final ProductService productService; // 제품 서비스 추가

    public void assignTagsToProducts () {
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
        //gpuName, screenSize 변수명 변경



        if (isGpuSuitableForSteamOrFPS(gpuName)) {
            tagNo = findTagByData(tags,"게이밍");
            assignedTags.add(tagNo);
            log.info("제품 {}에 '게이밍' 태그(#{}) 할당",  tagNo);
        }else {
            log.warn("'게이밍' 태그를 찾을 수 없습니다.");
        }
        if (isGpuSuitableForOnlineGames(gpuName)) {
            tagNo = findTagByData(tags, "펠월드");
            assignedTags.add(tagNo);
            log.info(" '온라인게임' 태그(#{}) 할당", tagNo);
        }
        if (isGpuSuitableForAOSGames(gpuName)) {
            tagNo = findTagByData(tags, "가성비");
            assignedTags.add(tagNo);
            log.info(" '가성비' 태그(#{}) 할당", tagNo);
        }

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



        return assignedTags;
    }

    private boolean isGpuSuitableForSteamOrFPS(String gpu) {
        if (gpu == null) {
            return false;
        }
        List<String> suitableGpus = List.of(
                "RTX4090", "RTX4080", "라데온 RX 7900M", "라데온 610M 라이젠 9 7845HX",
                "RTX3080Ti", "RTX4070", "RTX3070Ti", "RTX4060",
                "라데온 RX 6850M XT", "RTX 3080", "RTX A5000", "RTX3070",
                "라데온 RX 6800S", "RTXA4000", "RTX2080", "RTX3050Ti"
        );
        for (String suitableGpu : suitableGpus) {
            if (gpu.contains(suitableGpu)) {
                return true;
            }
        }
        return false;
    }

    private boolean isGpuSuitableForOnlineGames(String gpu) {
        if (gpu == null) {
            return false;
        }
        List<String> suitableGpus = List.of(
                "라데온 RX 6650M", "라데온 RX 6700S", "쿼드로 RTX 5000", "RTX4050",
                "라데온 RX 7600S", "라데온 RX 6850M XT", "RTX2080 SUPER", "RTX2070 SUPER",
                "라데온 RX 6600M", "라데온 RX 6600S", "라데온 Pro W6600M", "라데온 RX 6700M",
                "RTX3060", "라데온 RX 6800M", "RTX2080", "쿼드로 RTX 4000",
                "RTX A3000", "RTX2070"
        );
        for (String suitableGpu : suitableGpus) {
            if (gpu.contains(suitableGpu)) {
                return true;
            }
        }
        return false;
    }

    private boolean isGpuSuitableForAOSGames(String gpu) {
        if (gpu == null) {
            return false;
        }
        List<String> suitableGpus = List.of(
                "쿼드로 P5200", "라데온 RX 6850M", "RTX2070", "GTX1080",
                "라데온 RX 7600M XT", "인텔 Arc A770M", "RTX2060", "쿼드로 RTX 3000",
                "GTX1070", "RTX3050", "GTX1660 Ti", "RTX A2000",
                "라데온 RX 6550M", "라데온 Pro 5600M", "쿼드로 P4000", "라데온 RX 5600M"
        );
        for (String suitableGpu : suitableGpus) {
            if (gpu.contains(suitableGpu)) {
                return true;
            }
        }
        return false;
    }

    private boolean isScreenSuitableForCoding(String screena) {
        if (screena == null) {
            return false;
        }
        List<String> suitableBingScreens = List.of("40.8cm(16인치)",
                "40.8cm(16인치)", "40.89cm(16.1인치)", "41.05cm(16.2인치)",
                "41.4cm(16.3인치)", "43.18cm(17인치)", "43.18cm(17인치)",
                "43.94cm(17.3인치)", "45.72cm(18인치)"
        );
        for (String suitableScreen : suitableBingScreens) {
            if (screena.contains(suitableScreen)) {
                log.info("큰 화면 태그 할당 : {} {} ", screena, suitableScreen);
                return true;
            }
        }
        return false;
    }

    private boolean isScreenSuitableForDocuments(String screenb) {
        if (screenb == null) {
            return false;
        }
        List<String> suitableScreens = List.of("33.02cm(13인치)", "33.78cm(13.3인치)",
                "33.78cm(13.3인치)", "34.03cm(13.4인치)", "34.29cm(13.5인치)",
                "34.54cm(13.6인치)", "35.05cm(13.8인치)", "35.3cm(13.9인치)",
                "35.56cm(14인치)", "35.56cm(14인치)", "35.8cm(14.1인치)",
                "35.97cm(14.2인치)", "36.6cm(14.4인치)", "36.8cm(14.5인치)",
                "38.1cm(15인치)", "38.86cm(15.3인치)", "39.11cm(15.4인치)",
                "39.62cm(15.6인치)", "39.62cm(15.6인치)"
        );
        for (String suitableScreen : suitableScreens) {
            if (screenb.contains(suitableScreen)) {
                log.info("중간 태그 할당 : {} {} ", screenb, suitableScreen);
                return true;
            }
        }
        return false;
    }

    private boolean isScreenSuitableForStudents(String screenc) {
        if (screenc == null) {
            return false;
        }
        List<String> suitableScreenss = List.of( "20.32cm(8인치)", "25.4cm(10인치)", "25.65cm(10.1인치)",
                "26.16cm(10.3인치)", "26.67cm(10.5인치)", "26.92cm(10.6인치)",
                "27.69cm(10.9인치)", "27.94cm(11인치)", "29.21cm(11.5인치)",
                "29.46cm(11.6인치)", "30.48cm(12인치)", "31.24cm(12.3인치)",
                "31.62cm(12.4인치)"
        );
        for (String suitableScreen : suitableScreenss) {
            if (screenc.contains(suitableScreen)) {
                log.info("작은 태그 할당 : {} {} ", screenc, suitableScreen);
                return true;
            }
        }
        return false;
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