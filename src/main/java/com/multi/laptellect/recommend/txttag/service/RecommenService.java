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

        for (int productNO : productNOs) {
            log.info("제품 {} 처리 시작", productNO);
            List<LaptopDetailsDTO> laptopDetails = productMapper.laptopProductDetails(productNO);

            if (!laptopDetails.isEmpty()) {
                log.info("제품 {}의 상세 정보 존재", laptopDetails);
                LaptopSpecDTO laptops = productService.getLaptopSpec(productNO, laptopDetails);// 제품 상세 정보 조회

                List<Integer> tags = determineTagsForProduct(laptops);

                tagMapper.insertProductTag(productNO, tags);

                log.info("태그 할당 완료 = {} ", productNO);

            } else {
                log.warn("상품 번호 {}에 대한 상세 정보가 없습니다.", productNO);
            }
        }
        log.info("태그 할당 프로세스 완료");
    }

    private List<Integer> determineTagsForProduct(LaptopSpecDTO laptopSpecDTO) {
        List<Integer> assignedTags = new ArrayList<>();
        List<TaggDTO> tags = tagMapper.findAllTag();
        int tagNo;


        String gpuName = laptopSpecDTO.getGpu().getGpuChipset();
        String screenSize = laptopSpecDTO.getDisplay().getScreenSize();



        if (isGpuSuitableForSteamOrFPS(gpuName)) {
            tagNo = findTagByData(tags,"게이밍");
            assignedTags.add(tagNo);
            log.info("제품 {}에 '게이밍' 태그(#{}) 할당",  tagNo);
        }
        if (isGpuSuitableForOnlineGames(gpuName)) {
            tagNo = findTagByData(tags, "온라인게임");
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

        return assignedTags;
    }

    private boolean isGpuSuitableForSteamOrFPS(String gpu) {
        if (gpu == null) {
            return false;
        }
        List<String> suitableGpus = List.of(
                "RTX 4090", "RTX 4080", "라데온 RX 7900M", "라데온 610M 라이젠 9 7845HX",
                "RTX 3080 Ti", "RTX 4070", "RTX 3070 Ti", "RTX 4060",
                "라데온 RX 6850M XT", "RTX 3080", "RTX A5000", "RTX 3070",
                "라데온 RX 6800S", "RTX A4000", "RTX 2080", "RTX3050 Ti"
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

    private boolean isScreenSuitableForCoding(String screenSize) {
        if (screenSize == null) {
            return false;
        }
        List<String> suitableScreens = List.of("18인치", "17인치", "17.3인치");
        for (String suitableScreen : suitableScreens) {
            if (screenSize.contains(suitableScreen)) {
                return true;
            }
        }
        return false;
    }

    private boolean isScreenSuitableForDocuments(String screenSize) {
        if (screenSize == null) {
            return false;
        }
        List<String> suitableScreens = List.of("15.6인치", "16인치", "15인치");
        for (String suitableScreen : suitableScreens) {
            if (screenSize.contains(suitableScreen)) {
                return true;
            }
        }
        return false;
    }

    private boolean isScreenSuitableForStudents(String screenSize) {
        if (screenSize == null) {
            return false;
        }
        List<String> suitableScreens = List.of("13.3인치", "14인치", "13인치");
        for (String suitableScreen : suitableScreens) {
            if (screenSize.contains(suitableScreen)) {
                return true;
            }
        }
        return false;
    }

    private boolean isDesignBeautiful(String model) {
        if (model == null) {
            return false;
        }
        List<String> beautifulModels = List.of(
                "MacBook Air (M1, 2020)", "MacBook Air (M2, 2022)", "MacBook Pro 13-inch (M1, 2020)",
                "MacBook Pro 14-inch (2021)", "MacBook Pro 16-inch (2021)", "MacBook Pro 13-inch (M2, 2022)",
                "LG Gram 14 (14Z90P)", "LG Gram 16 (16Z90P)", "LG Gram 17 (17Z90P)",
                "LG Gram 2-in-1 14 (14T90P)", "LG Gram 2-in-1 16 (16T90P)", "LG Gram SuperSlim (15Z90RT)",
                "LG Gram Style (16Z90RS)"
        );
        for (String beautifulModel : beautifulModels) {
            if (model.contains(beautifulModel)) {
                return true;
            }
        }
        return false;
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