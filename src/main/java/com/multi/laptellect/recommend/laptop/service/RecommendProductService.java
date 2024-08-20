package com.multi.laptellect.recommend.laptop.service;

import com.multi.laptellect.product.model.dto.LaptopDetailsDTO;
import com.multi.laptellect.product.model.dto.laptop.LaptopSpecDTO;
import com.multi.laptellect.product.model.mapper.ProductMapper;
import com.multi.laptellect.product.service.ProductService;
import com.multi.laptellect.recommend.laptop.model.dao.RecommendProductDAO;
import com.multi.laptellect.recommend.laptop.model.dto.CurationDTO;
import com.multi.laptellect.recommend.laptop.model.dto.ProductFilterDTO;
import com.multi.laptellect.recommend.service.RedisCacheService;
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
    private final ProductMapper productMapper;
    private final ProductService productService;
    private final RecommendProductDAO recommendProductDAO;
    private final RedisCacheService redisCacheService;

    public ArrayList<LaptopSpecDTO> getRecommendations(CurationDTO curationDTO) {
        ProductFilterDTO productFilterDTO = createSearchCriteria(curationDTO);
        ArrayList<Integer> productNos = recommendProductDAO.findLaptopDetailByFilter(productFilterDTO);
        ArrayList<LaptopSpecDTO> laptop = new ArrayList<>();

        for(int productNo : productNos) {
            List<LaptopDetailsDTO> laptopDetails = productMapper.laptopProductDetails(productNo);
            if (!laptopDetails.isEmpty()) {
                laptop.add(productService.getLaptopSpec(productNo, laptopDetails));
            }
        }
        return laptop;
    }

    public List<TaggDTO> getTagsForProduct(int productNo) {
        return recommendProductDAO.getTagsForProduct(productNo);
    }

    public ArrayList<LaptopSpecDTO> getCachedCurationResult(String cacheKey) {
        try {
            CurationDTO curationDTO = redisCacheService.getCurationResult(cacheKey);
            if (curationDTO != null) {
                return getRecommendations(curationDTO);
            }
        } catch (Exception e) {
            log.error("캐시된 결과를 가져오는 중 오류 발생", e);
        }
        return new ArrayList<>();
    }

    private ProductFilterDTO createSearchCriteria(CurationDTO curationDTO) {
        ProductFilterDTO productFilterDTO = new ProductFilterDTO();
        String mainOption = curationDTO.getMainOption();

        if (mainOption.equals("게임 할거에요")) {
            productFilterDTO.setGpu(getGpuTags(curationDTO.getGpu()));
        } else if (mainOption.equals("작업 할거에요")) {
            productFilterDTO.setCpu(getCpuTags(curationDTO.getCpu()));
        }

        productFilterDTO.setWeightTags(getWeightTags(curationDTO.getWeight()));
        productFilterDTO.setGamingTags(getGamingTags(curationDTO.getPerformance()));
        productFilterDTO.setScreen(getScreenTags(curationDTO.getScreen()));
        productFilterDTO.setSomoweightTags(getSomoWeight(curationDTO.getSomoweight()));

        return productFilterDTO;
    }

    private List<String> getSomoWeight(String somoweight) {
        log.info("무게 태그 설정 시작. 무게: {}", somoweight);
        if (somoweight == null) {
            return List.of();
        }
        switch (somoweight) {
            case "경량화":
                return List.of("경량화");
            case "무거움":
                return List.of("무거움");
            case "초경량화":
                return List.of("초경량화");
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

    private List<String> getGpuTags(String gameType) {
        if (gameType == null) {
            return List.of();
        }
        switch (gameType) {
            case "스팀게임/FPS 게임":
                return List.of("펠월드", "로스트 아크");
            case "온라인 게임":
                return List.of("배틀그라운드");
            case "AOS게임":
                return List.of("리그오브레전드", "게이밍");
            default:
                return List.of();
        }
    }

    private List<String> getCpuTags(String purpose) {
        if (purpose == null) {
            return List.of();
        }
        switch (purpose) {
            case "코드 작업할거에요":
                return List.of("작업용");
            case "일반 사무용 작업할거에요":
                return List.of("인터넷 강의");
            default:
                return List.of();
        }
    }

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

    private List<String> getScreenTags(String screen) {
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
}