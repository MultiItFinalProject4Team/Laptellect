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

    private final ProductMapper productMapper;
    private final ProductTagDAO tagMapper;
    private final ProductService productService;

    public void assignTagsToProducts() {
        log.info("태그 할당 프로세스 시작");
        List<TaggDTO> tags = tagMapper.getAllTags();
        log.info("전체 태그 수: {}", tags.size());
        List<LaptopSpecDTO> products = tagMapper.getAllProducts();
        log.info("전체 제품 수: {}", products.size());

        for (LaptopSpecDTO product : products) {
            log.info("제품 {} 처리 시작", product.getProductNo());
            List<LaptopDetailsDTO> laptopDetails = productMapper.laptopProductDetails(product.getProductNo());

            if (!laptopDetails.isEmpty()) { // 상세 정보가 존재하는 경우에만 태그 할당
                log.info("제품 {}의 상세 정보 존재", product.getProductNo()); // 상세 정보가 존재하는 경우에만 태그 할당
                LaptopSpecDTO laptopSpec = productService.getLaptopSpec(product.getProductNo(), laptopDetails); // 상세 정보로 제품 스펙 추출
                List<Integer> assignedTags = determineTagsForProduct(laptopSpec, tags); // 제품 스펙을 기반으로 태그 결정

                for (Integer tagNo : assignedTags) { // 결정된 태그를 제품에 할당
                    tagMapper.insertProductTag(product.getProductNo(), tagNo); // 태그 할당
                    log.info("제품 {}에 태그 {} 삽입 완료", product.getProductNo(), tagNo);
                }

                log.info("노트북에 할당된 태그 {}: {}", product.getProductNo(), assignedTags);
            } else {
                log.warn("상품 번호 {}에 대한 상세 정보가 없습니다.", product.getProductNo());
            }
        }
        log.info("태그 할당 프로세스 완료");
    }

    private List<Integer> determineTagsForProduct(LaptopSpecDTO laptop, List<TaggDTO> tags) {
        List<Integer> assignedTags = new ArrayList<>(); // 할당된 태그 목록

        log.info("제품 {} 태그 결정 시작", laptop.getProductNo());

        if (isGpuSuitableForSteamOrFPS(laptop.getGpu().getGpuChipset())) {
            int tagNo = findTagByData(tags, "게이밍"); // 게이밍 태그 찾기
            assignedTags.add(tagNo); // 게이밍 태그 할당
            log.info("제품 {}에 '게이밍' 태그(#{}) 할당", laptop.getProductNo(), tagNo);
        }
        if (isGpuSuitableForOnlineGames(laptop.getGpu().getGpuChipset())) {
            int tagNo = findTagByData(tags, "온라인게임");
            assignedTags.add(tagNo);
            log.info("제품 {}에 '온라인게임' 태그(#{}) 할당", laptop.getProductNo(), tagNo);
        }
        if (isGpuSuitableForAOSGames(laptop.getGpu().getGpuChipset())) {
            int tagNo = findTagByData(tags, "가성비");
            assignedTags.add(tagNo);
            log.info("제품 {}에 '가성비' 태그(#{}) 할당", laptop.getProductNo(), tagNo);
        }

        if (isScreenSuitableForCoding(laptop.getDisplay().getScreenSize())) {
            int tagNo = findTagByData(tags, "넓은 화면");
            assignedTags.add(tagNo);
            log.info("제품 {}에 '넓은 화면' 태그(#{}) 할당", laptop.getProductNo(), tagNo);
        }
        if (isScreenSuitableForDocuments(laptop.getDisplay().getScreenSize())) {
            int tagNo = findTagByData(tags, "작은 화면");
            assignedTags.add(tagNo);
            log.info("제품 {}에 '작은 화면' 태그(#{}) 할당", laptop.getProductNo(), tagNo);
        }
        if (isScreenSuitableForStudents(laptop.getDisplay().getScreenSize())) {
            int tagNo = findTagByData(tags, "적당한 화면");
            assignedTags.add(tagNo);
            log.info("제품 {}에 '적당한 화면' 태그(#{}) 할당", laptop.getProductNo(), tagNo);
        }

        if (isDesignBeautiful(laptop.getProductName())) {
            int tagNo = findTagByData(tags, "예쁜 디자인");
            assignedTags.add(tagNo);
            log.info("제품 {}에 '예쁜 디자인' 태그(#{}) 할당", laptop.getProductNo(), tagNo);
        }
        
        log.info("제품 {} 태그 결정 완료. 할당된 태그: {}", laptop.getProductNo(), assignedTags);
        return assignedTags;
    }

    private boolean isGpuSuitableForSteamOrFPS(String gpu) {
        List<String> suitableGpus = List.of(
                "RTX 4090", "RTX 4080", "라데온 RX 7900M", "라데온 610M 라이젠 9 7845HX",
                "RTX 3080 Ti", "RTX 4070", "RTX 3070 Ti", "RTX 4060",
                "라데온 RX 6850M XT", "RTX 3080", "RTX A5000", "RTX 3070",
                "라데온 RX 6800S", "RTX A4000", "RTX 2080"
        );
        for (String suitableGpu : suitableGpus) { // 게이밍 태그에 적합한 GPU인지 확인
            if (gpu.contains(suitableGpu)) { // GPU에 적합한 경우
                return true; // 적합한 GPU인 경우
            }
        }
        return false; // 적합하지 않은 GPU인 경우
    }

    private boolean isGpuSuitableForOnlineGames(String gpu) {
        List<String> suitableGpus = List.of(
                "GTX 1050", "GTX 1050 Ti", "GTX 1650", "GTX 1060", "GTX 1660", "GTX 1660 Ti",
                "RTX 2060", "RTX 2070", "RTX 2080", "RTX 2070 ", "RTX 2080 ",
                "RTX 3060", "RTX 3070", "RTX 3080", "RTX 3090",
                "RTX 4050", "RTX 4060", "RTX 4070", "RTX 4080", "RTX 4090",
                "RX 560", "RX 570", "RX 580", "RX 590",
                "RX 5500 XT", "RX 5600 XT", "RX 5700", "RX 5700 XT"
        );
        for (String suitableGpu : suitableGpus) {
            if (gpu.contains(suitableGpu)) {
                return true;
            }
        }
        return false;
    }

    private boolean isGpuSuitableForAOSGames(String gpu) {
        List<String> suitableGpus = List.of(
                "라데온 RX 7900M", "라데온 RX 6850M XT", "라데온 RX 6800M", "라데온 RX 6700M", "라데온 RX 6650M", "라데온 RX 6600M",
                "라데온 RX 6500M", "Radeon 740M", "RTX 4090", "RTX 4080", "RTX 4070", "RTX 4060", "RTX 4050", "RTX 3080 Ti",
                "RTX 3070 Ti", "RTX 3060", "I9-14900HX", "I9-13980HX", "I7-13700HX", "I5-13500HX", "I9-13900H", "I7-13700H",
                "I5-13500H", "Ryzen 9 7945HX", "Ryzen 7 7745HX", "Ryzen 5 7645HX", "Ryzen 9 7940HS", "Ryzen 7 7840HS",
                "Ryzen 5 7640HS"
        );
        for (String suitableGpu : suitableGpus) {
            if (gpu.contains(suitableGpu)) {
                return true;
            }
        }
        return false;
    }

    private boolean isScreenSuitableForCoding(String screen) {
        List<String> suitableScreens = List.of("18인치", "17인치", "17.3인치");
        for (String suitableScreen : suitableScreens) {
            if (screen.contains(suitableScreen)) {
                return true;
            }
        }
        return false;
    }

    private boolean isScreenSuitableForDocuments(String screen) {
        List<String> suitableScreens = List.of("15.6인치", "16인치", "15인치");
        for (String suitableScreen : suitableScreens) {
            if (screen.contains(suitableScreen)) {
                return true;
            }
        }
        return false;
    }

    private boolean isScreenSuitableForStudents(String screen) {
        List<String> suitableScreens = List.of("13.3인치", "14인치", "13인치");
        for (String suitableScreen : suitableScreens) {
            if (screen.contains(suitableScreen)) {
                return true;
            }
        }
        return false;
    }

    private boolean isDesignBeautiful(String model) {
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

    private boolean isHighPerformance(String cpu, String gpu) {
        List<String> highPerformanceCombos = List.of(
                "AMD Ryzen 9 7945HX3D GeForce RTX 4090",
                "Intel Core i9-14900HX GeForce RTX 4080",
                "AMD Ryzen 9 7945HX Radeon RX 7900M",
                "Intel Core i9-13980HX GeForce RTX 3080 Ti",
                "AMD Ryzen 9 7940HS Radeon RX 6850M XT",
                "Intel Core i9-13905H GeForce RTX 3080",
                "AMD Ryzen 9 6980HX Radeon RX 6800M",
                "Intel Core i9-12900HX RTX A5000",
                "Apple M3 Max"
        );
        String combo = cpu + " " + gpu;
        for (String highPerformanceCombo : highPerformanceCombos) {
            if (combo.contains(highPerformanceCombo)) {
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