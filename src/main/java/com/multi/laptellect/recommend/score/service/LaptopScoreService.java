package com.multi.laptellect.recommend.score.service;

import com.multi.laptellect.recommend.score.model.dao.LaptopScoreDAO;
import com.multi.laptellect.recommend.score.model.dto.DetailsDTO;
import com.multi.laptellect.recommend.score.model.dto.LaptopScoreDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LaptopScoreService {
    private final LaptopScoreDAO laptopScoreDAO;

    public void calculateAndSaveLaptopScores() {
        List<DetailsDTO> laptops = laptopScoreDAO.getAllLaptops();
        for (DetailsDTO laptop : laptops) {
            Map<String, String> specs = laptopScoreDAO.getLaptopSpecs(laptop.getProductNo());
            laptop.setSpecs(specs);
            LaptopScoreDTO scoreDTO = calculateLaptopScores(laptop);
            laptopScoreDAO.saveOrUpdateLaptopScore(scoreDTO);
        }
    }

    private LaptopScoreDTO calculateLaptopScores(DetailsDTO laptop) {
        LaptopScoreDTO scoreDTO = new LaptopScoreDTO();
        scoreDTO.setProductNo(laptop.getProductNo());

        // CPU 점수 계산
        String cpu = laptop.getSpecs().get("CPU");
        scoreDTO.setCpuScore(calculateCpuScore(cpu));

        // GPU 점수 계산
        String gpu = laptop.getSpecs().get("GPU");
        scoreDTO.setGpuScore(calculateGpuScore(gpu));

        // RAM 점수 계산
        String ram = laptop.getSpecs().get("RAM");
        scoreDTO.setRamScore(calculateRamScore(ram));

        // 저장장치 점수 계산
        String storage = laptop.getSpecs().get("Storage");
        scoreDTO.setStorageScore(calculateStorageScore(storage));

        // 가격 점수 계산
        int price = laptop.getPrice();
        scoreDTO.setPriceScore(calculatePriceScore(price));

        // 무게 점수 계산
        String weight = laptop.getSpecs().get("Weight");
        scoreDTO.setWeightScore(calculateWeightScore(weight));

        // 화면 크기 점수 계산
        String screenSize = laptop.getSpecs().get("ScreenSize");
        scoreDTO.setScreenSizeScore(calculateScreenSizeScore(screenSize));

        // 해상도 점수 계산
        String resolution = laptop.getSpecs().get("Resolution");
        scoreDTO.setResolutionScore(calculateResolutionScore(resolution));

        return scoreDTO;
    }

    private int calculateCpuScore(String cpu) {
        // CPU 점수 계산 로직
        // 예: Intel Core i7, AMD Ryzen 7 등의 성능에 따라 점수 부여
        if (cpu.contains("i9") || cpu.contains("Ryzen 9")) return 20;
        if (cpu.contains("i7") || cpu.contains("Ryzen 7")) return 15;
        if (cpu.contains("i5") || cpu.contains("Ryzen 5")) return 10;
        return 5; // 기본 점수
    }

    private int calculateGpuScore(String gpu) {
        // GPU 점수 계산 로직
        // 예: NVIDIA RTX 3080, AMD Radeon RX 6800 등의 성능에 따라 점수 부여
        if (gpu.contains("RTX 3080") || gpu.contains("RX 6800 XT")) return 50;
        if (gpu.contains("RTX 3070") || gpu.contains("RX 6700 XT")) return 40;
        if (gpu.contains("RTX 3060") || gpu.contains("RX 6600 XT")) return 30;
        return 10; // 기본 점수 또는 내장 그래픽
    }

    private int calculateRamScore(String ram) {
        // RAM 점수 계산 로직
        int ramSize = Integer.parseInt(ram.replaceAll("\\D+",""));
        if (ramSize >= 32) return 10;
        if (ramSize >= 16) return 8;
        if (ramSize >= 8) return 5;
        return 3; // 4GB 이하
    }

    private int calculateStorageScore(String storage) {
        // 저장장치 점수 계산 로직
        if (storage.contains("1TB") && storage.contains("NVMe")) return 10;
        if (storage.contains("512GB") && storage.contains("NVMe")) return 8;
        if (storage.contains("256GB") && storage.contains("SSD")) return 5;
        return 3; // HDD or smaller SSD
    }

    private int calculatePriceScore(int price) {
        // 가격 점수 계산 로직 (예시)
        if (price <= 500000) return 10; // 매우 저렴
        if (price <= 1000000) return 8;
        if (price <= 1500000) return 6;
        if (price <= 2000000) return 4;
        return 2; // 고가 제품
    }

    private int calculateWeightScore(String weight) {
        // 무게 점수 계산 로직
        double weightKg = Double.parseDouble(weight.replaceAll("[^\\d.]", ""));
        if (weightKg < 1.5) return 5;
        if (weightKg < 2.0) return 4;
        if (weightKg < 2.5) return 2;
        return 1;
    }

    private int calculateScreenSizeScore(String screenSize) {
        // 화면 크기 점수 계산 로직
        double size = Double.parseDouble(screenSize.replaceAll("[^\\d.]", ""));
        if (size >= 17) return 5;
        if (size >= 15) return 4;
        if (size >= 13) return 3;
        return 2;
    }

    private int calculateResolutionScore(String resolution) {
        // 해상도 점수 계산 로직
        if (resolution.contains("4K") || resolution.contains("3840x2160")) return 5;
        if (resolution.contains("2K") || resolution.contains("2560x1440")) return 4;
        if (resolution.contains("1920x1080") || resolution.contains("Full HD")) return 3;
        return 2; // HD 또는 그 이하
    }
}