package com.multi.laptellect.recommend.txttag.service;

import com.multi.laptellect.recommend.txttag.model.dao.ProductDAO2;
import com.multi.laptellect.recommend.txttag.model.dao.ProductTagDAO;
import com.multi.laptellect.recommend.txttag.model.dao.TagDAO2;
import com.multi.laptellect.recommend.txttag.model.dto.ProductDTO2;
import com.multi.laptellect.recommend.txttag.model.dto.TaggDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class RecommenService {
    @Autowired
    private ProductDAO2 productMapper;
    @Autowired
    private TagDAO2 tagMapper;
    @Autowired
    private ProductTagDAO productTagMapper;

    public void assignTagsToProducts() {
        List<ProductDTO2> products = productMapper.getAllProducts();
        List<TaggDTO> tags = tagMapper.getAllTags();

        for (ProductDTO2 product : products) {
            try {
                productTagMapper.deleteProductTags(product.getProductNo());
                List<Integer> assignedTags = determineTagsForProduct(product, tags);
                for (Integer tagNo : assignedTags) {
                    productTagMapper.insertProductTag(product.getProductNo(), tagNo);
                }
                log.info("Tags assigned to product {}: {}", product.getProductNo(), assignedTags);
            } catch (Exception e) {
                log.error("Error assigning tags to product {}: {}", product.getProductNo(), e.getMessage());
            }
        }
    }

    private List<Integer> determineTagsForProduct(ProductDTO2 product, List<TaggDTO> tags) {
        List<Integer> assignedTags = new ArrayList<>();

        String gpu = product.getGpu().toLowerCase();
        if (isGpuSuitableForSteamOrFPS(gpu)) {
            assignedTags.add(findTagByData(tags, "스팀게임/FPS 게임"));
        }
        if (isGpuSuitableForOnlineGames(gpu)) {
            assignedTags.add(findTagByData(tags, "온라인 게임"));
        }
        if (isGpuSuitableForAOSGames(gpu)) {
            assignedTags.add(findTagByData(tags, "AOS게임"));
        }

        String cpu = product.getCpu().toLowerCase();
        if (isCpuSuitableForCoding(cpu)) {
            assignedTags.add(findTagByData(tags, "코딩용"));
        }
        if (isCpuSuitableForDocuments(cpu)) {
            assignedTags.add(findTagByData(tags, "문서용"));
        }
        if (isCpuSuitableForStudents(cpu)) {
            assignedTags.add(findTagByData(tags, "학생용/인강용"));
        }

        double weight = Double.parseDouble(product.getWeight().replace("kg", ""));
        if (weight <= 1.5) {
            assignedTags.add(findTagByData(tags, "가벼워요"));
        } else if (weight >= 2.5) {
            assignedTags.add(findTagByData(tags, "무거워요"));
        }

        double screenSize = Double.parseDouble(product.getScreenSize().replace("cm", "").replace("(", "").split("인치")[0]);
        if (screenSize >= 17.0) {
            assignedTags.add(findTagByData(tags, "넓은 화면"));
        } else if (screenSize >= 15.0 && screenSize < 17.0) {
            assignedTags.add(findTagByData(tags, "적당한 화면"));
        } else {
            assignedTags.add(findTagByData(tags, "작은 화면"));
        }

        int batteryCapacity = Integer.parseInt(product.getBatteryCapacity().replace("Wh", ""));
        if (batteryCapacity >= 70) {
            assignedTags.add(findTagByData(tags, "오래 가는 배터리"));
        } else {
            assignedTags.add(findTagByData(tags, "짧은 배터리"));
        }

        if (isDesignBeautiful(product.getProductName())) {
            assignedTags.add(findTagByData(tags, "예쁜 디자인"));
        }

        if (isHighPerformance(cpu, gpu)) {
            assignedTags.add(findTagByData(tags, "동세대 최고 성능"));
        }

        return assignedTags;
    }

    private boolean isGpuSuitableForSteamOrFPS(String gpu) {
        List<String> suitableGpus = Arrays.asList(
                "geforce rtx 4090", "geforce rtx 4080", "radeon rx 7900m", "radeon 610m ryzen 9 7845hx",
                "geforce rtx 3080 ti", "geforce rtx 4070", "geforce rtx 3070 ti", "geforce rtx 4060",
                "radeon rx 6850m xt", "geforce rtx 3080", "rtx a5000", "geforce rtx 3070",
                "radeon rx 6800s", "rtx a4000", "geforce rtx 2080"
        );
        for (String suitableGpu : suitableGpus) {
            if (gpu.contains(suitableGpu)) {
                return true;
            }
        }
        return false;
    }

    private boolean isGpuSuitableForOnlineGames(String gpu) {
        List<String> suitableGpus = Arrays.asList(
                "radeon rx 6650m", "radeon rx 6700s", "quadro rtx 5000", "geforce rtx 4050",
                "radeon rx 7600s", "radeont rx 6850m xt", "geforce rtx 2080 super", "geforce rtx 2070 super",
                "radeon rx 6600m", "radeon rx 6600s", "radeon pro w6600m", "radeon rx 6700m",
                "geforce rtx 3060", "radeon rx 6800m", "geforce rtx 2080", "quadro rtx 4000",
                "rtx a3000", "geforce rtx 2070"
        );
        for (String suitableGpu : suitableGpus) {
            if (gpu.contains(suitableGpu)) {
                return true;
            }
        }
        return false;
    }

    private boolean isGpuSuitableForAOSGames(String gpu) {
        List<String> suitableGpus = Arrays.asList(
                "quadro p5200", "radeon rx 6850m", "geforce rtx 2070", "geforce gtx 1080",
                "radeon rx 7600m xt", "intel arc a770m", "geforce rtx 2060", "quadro rtx 3000",
                "geforce gtx 1070", "geforce rtx 3050", "geforce gtx 1660 ti", "rtx a2000",
                "radeon rx 6550m", "radeon pro 5600m", "radeon rx 5600m"
        );
        for (String suitableGpu : suitableGpus) {
            if (gpu.contains(suitableGpu)) {
                return true;
            }
        }
        return false;
    }

    private boolean isCpuSuitableForCoding(String cpu) {
        List<String> suitableCpus = Arrays.asList(
                "amd ryzen 9 7945hx3d", "amd ryzen 9 7945hx", "amd ryzen 9 7940hx",
                "intel core i9-13980hx", "intel core i9-14900hx", "amd ryzen 9 7845hx",
                "intel core i9-13900hx", "intel core i9-13950hx", "intel core i7-14650hx",
                "intel core i7-13850hx", "intel core i7-14700hx", "intel core i9-12900hx",
                "intel core i7-13700hx", "amd ryzen 7 7745hx", "intel core i9-12950hx",
                "intel core i7-12800hx", "amd ryzen 9 8945h", "intel core i9-13900hk",
                "intel core i7-13650hx", "intel core i7-12850hx"
        );
        for (String suitableCpu : suitableCpus) {
            if (cpu.contains(suitableCpu)) {
                return true;
            }
        }
        return false;
    }

    private boolean isCpuSuitableForDocuments(String cpu) {
        List<String> suitableCpus = Arrays.asList(
                "intel core i5-1235u", "amd ryzen 3 5425u", "intel pentium gold 7505",
                "intel core i3-1215u", "amd ryzen 3 5300u", "intel core i3-1115g4",
                "intel core i5-10210u", "amd ryzen 3 3250u", "intel core i3-1005g1",
                "intel pentium gold 6405u", "intel core i3-8145u", "amd athlon gold 3150u",
                "intel celeron 6305", "amd athlon silver 3050u", "intel pentium 6805"
        );
        for (String suitableCpu : suitableCpus) {
            if (cpu.contains(suitableCpu)) {
                return true;
            }
        }
        return false;
    }

    private boolean isCpuSuitableForStudents(String cpu) {
        List<String> suitableCpus = Arrays.asList(
                "intel core i5-12500h", "amd ryzen 5 5600h", "intel core i5-11400h",
                "amd ryzen 5 4600h", "intel core i5-10300h", "amd ryzen 5 4500u",
                "intel core i5-1135g7", "amd ryzen 5 5500u", "intel core i5-10210u",
                "amd ryzen 5 3500u", "intel core i7-1165g7", "amd ryzen 7 4700u",
                "intel core i7-10510u", "amd ryzen 7 5700u", "intel core i7-1185g7"
        );
        for (String suitableCpu : suitableCpus) {
            if (cpu.contains(suitableCpu)) {
                return true;
            }
        }
        return false;
    }

    private boolean isDesignBeautiful(String model) {
        List<String> beautifulModels = Arrays.asList(
                "macbook air (m1, 2020)", "macbook air (m2, 2022)", "macbook pro 13-inch (m1, 2020)",
                "macbook pro 14-inch (2021)", "macbook pro 16-inch (2021)", "macbook pro 13-inch (m2, 2022)",
                "lg gram 14 (14z90p)", "lg gram 16 (16z90p)", "lg gram 17 (17z90p)",
                "lg gram 2-in-1 14 (14t90p)", "lg gram 2-in-1 16 (16t90p)", "lg gram superslim (15z90rt)",
                "lg gram style (16z90rs)"
        );
        for (String beautifulModel : beautifulModels) {
            if (model.toLowerCase().contains(beautifulModel)) {
                return true;
            }
        }
        return false;
    }

    private boolean isHighPerformance(String cpu, String gpu) {
        List<String> highPerformanceCombos = Arrays.asList(
                "amd ryzen 9 7945hx3d geforce rtx 4090",
                "intel core i9-14900hx geforce rtx 4080",
                "amd ryzen 9 7945hx radeon rx 7900m",
                "intel core i9-13980hx geforce rtx 3080 ti",
                "amd ryzen 9 7940hs radeon rx 6850m xt",
                "intel core i9-13905h geforce rtx 3080",
                "amd ryzen 9 6980hx radeon rx 6800m",
                "intel core i9-12900hx rtx a5000",
                "apple m3 max"
        );
        String combo = (cpu + " " + gpu).toLowerCase();
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
                return tag.getTagNo();
            }
        }
        return -1;
    }
}