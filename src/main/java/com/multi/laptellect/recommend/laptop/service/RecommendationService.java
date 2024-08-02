package com.multi.laptellect.recommend.laptop.service;


import com.multi.laptellect.recommend.laptop.model.dao.ProductDAO;
import com.multi.laptellect.recommend.laptop.model.dto.LaptopDTO;
import com.multi.laptellect.recommend.laptop.model.dto.RecommendationRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecommendationService {
    @Autowired
    private ProductDAO productDAO;

    public List<LaptopDTO> getRecommendations(RecommendationRequestDTO request) {
        List<String> tags = buildTags(request);
        List<LaptopDTO> products = productDAO.findByTags(tags);
        return products.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private List<String> buildTags(RecommendationRequestDTO request) {
        List<String> tags = new ArrayList<>();

        // 용도
        if ("gaming".equals(request.getUsage())) {
            tags.add("gaming");
            // 게임 종류에 따른 태그 추가
            if ("steam-fps".equals(request.getGameType())) { //FPS
                tags.addAll(Arrays.asList("NVIDIA GeForce RTX 4090 Laptop GPU", "NVIDIA GeForce RTX 4080 Laptop GPU",
                        "NVIDIA GeForce RTX 4070 Laptop GPU", "NVIDIA GeForce RTX 3080 Ti Laptop GPU",
                        "NVIDIA GeForce RTX 3080 Laptop GPU", "AMD Radeon RX 6850M XT"));
            } else if ("online".equals(request.getGameType())) { //온라인
                tags.addAll(Arrays.asList("NVIDIA GeForce RTX 3070 Ti Laptop GPU", "NVIDIA GeForce RTX 4060 Laptop GPU",
                        "AMD Radeon RX 6800M", "NVIDIA GeForce RTX 2080", "NVIDIA GeForce RTX 3070 Laptop GPU",
                        "AMD Radeon RX 6800S", "AMD Radeon RX 7600M XT", "AMD Radeon RX 7700S"));
            } else if ("aos".equals(request.getGameType())) { //AOS
                tags.addAll(Arrays.asList("NVIDIA GeForce GTX 1080", "NVIDIA GeForce GTX 1070", "NVIDIA GeForce RTX 2070",
                        "NVIDIA GeForce RTX 3060", "NVIDIA GeForce GTX 980 SLI", "NVIDIA GeForce RTX 4050 Laptop GPU",
                        "NVIDIA GeForce RTX 3050 Ti Laptop GPU", "NVIDIA GeForce RTX 3050 6GB Laptop GPU",
                        "NVIDIA GeForce GTX 980M", "NVIDIA GeForce GTX 1660 Ti", "NVIDIA GeForce GTX 1060",
                        "NVIDIA GeForce GTX 1650 Ti", "NVIDIA GeForce GTX 880M", "NVIDIA GeForce GTX 780M",
                        "NVIDIA GeForce GTX 965M", "NVIDIA GeForce GTX 970M", "NVIDIA GeForce GTX 960M"));
            }
        } else if ("office".equals(request.getUsage())) {
            tags.add("office"); //사무용
            if ("coding".equals(request.getOfficeType())) { //코딩용
                tags.addAll(Arrays.asList("AMD Ryzen 9 7945HX3D", "AMD Ryzen 9 7945HX", "AMD Ryzen 9 7940HX",
                        "Intel Core i9-13980HX", "Intel Core i9-14900HX", "AMD Ryzen 9 7845HX", "Intel Core i9-13900HX",
                        "Intel Core i9-13950HX", "Apple M3 Max 16 Core", "Intel Core i7-14650HX", "Intel Core i7-13850HX",
                        "Intel Core i7-14700HX", "Apple M3 Max 14 Core", "Intel Core i9-12900HX", "Intel Core i7-13700HX",
                        "AMD Ryzen 7 7745HX", "Intel Core i9-12950HX", "Intel Core i7-12800HX", "AMD Ryzen 9 8945H",
                        "Intel Core i9-13900HK", "Intel Core i7-13650HX", "Intel Core i7-12850HX", "AMD Ryzen 9 7940HS",
                        "Intel Core i9-13905H", "Intel Core i5-13500HX", "AMD Ryzen 9 8945HS", "Intel Core Ultra 9 185H",
                        "Intel Core i9-13900H", "AMD Ryzen 7 7840HS", "AMD Ryzen 7 8845HS", "AMD Ryzen 7 8845H",
                        "AMD Ryzen 7 PRO 8845HS", "AMD Ryzen 9 7940H", "AMD Ryzen 7 7840H", "AMD Ryzen 7 PRO 8840HS",
                        "Intel Core i5-14500HX", "AMD Ryzen 9 PRO 7940HS", "Intel Core i9-12900H", "Intel Core i9-12900HK",
                        "Intel Core i5-13600HX", "Intel Core i7-13700H", "Apple M3 Pro 12 Core", "Intel Core i7-13800H",
                        "AMD Ryzen 5 7645HX", "AMD Ryzen 7 PRO 7840HS", "Apple M2 Max 12 Core", "Apple M2 Pro 12 Core",
                        "Intel Core Ultra 7 165H", "Intel Core i7-12700H", "Intel Core i5-13450HX", "Intel Core i7-13620H",
                        "Intel Core Ultra 7 155H", "AMD Ryzen 7 PRO 8840U", "AMD Ryzen 7 7840U", "AMD Ryzen 7 PRO 7840U",
                        "AMD Ryzen 7 7435HS", "Intel Core i7-12800H", "AMD Ryzen 7 8840HS", "AMD Ryzen 9 6900HX",
                        "AMD Ryzen 7 7840S", "Apple M3 Pro 11 Core", "Intel Core i5-12600HX", "Intel Core i5-13600H",
                        "AMD Ryzen 7 7735H", "AMD Ryzen 7 7735HS", "AMD Ryzen 7 8840U", "Intel Core Ultra 5 135H",
                        "AMD Ryzen 9 6900HS", "Intel Core i7-13705H", "AMD Ryzen 9 5980HX", "AMD Ryzen 7 6800H",
                        "AMD Ryzen 5 8645HS", "Intel Core i7-13700TE", "AMD Ryzen 7 6800HS", "AMD Ryzen 7 PRO 6850H",
                        "Intel Core i9-11980HK", "Intel Core i5-13500H", "Intel Core i5-12600H", "AMD Ryzen 5 PRO 7640HS",
                        "Intel Core i7-12650H", "AMD Ryzen 5 7640HS", "AMD Ryzen 9 PRO 6950HS", "Intel Core Ultra 5 125H",
                        "Intel Xeon W-11955M", "AMD Ryzen 9 5900HX", "AMD Ryzen 7 7736U", "AMD Ryzen 9 5900HS Creator Edition",
                        "AMD Ryzen 7 PRO 6850HS", "Apple M1 Max 10 Core", "AMD Ryzen 5 PRO 8640HS",
                        "AMD Ryzen 9 6900HS Creator Edition", "AMD Ryzen 7 6800HS Creator Edition", "Apple M1 Pro 10 Core",
                        "AMD Ryzen 9 5900HS", "Apple M2 Pro 10 Core", "AMD Ryzen 5 7640U", "AMD Ryzen 9 5980HS",
                        "AMD Ryzen 7 7735U", "Intel Core i9-11950H", "AMD Ryzen 5 8640HS", "Intel Core i5-12500H",
                        "AMD Ryzen 7 5800HS Creator Edition", "AMD Ryzen 7 5800H", "AMD Ryzen 7 PRO 6860Z",
                        "AMD Ryzen 7 PRO 6850U", "Intel Core i9-11900H", "AMD Ryzen 5 8640U", "AMD Ryzen 7 6800U",
                        "Intel Core i7-11850H", "AMD Ryzen 9 5900H", "Intel Core i7-11800H", "Intel Core i7-1280P",
                        "AMD Ryzen 7 Pro 7735U", "Intel Core i7-1370P", "Intel Core i5-1250P", "AMD Ryzen 7 5800HS",
                        "Apple M3 8 Core", "Intel Core i7-1360P", "AMD Ryzen 5 8540U", "AMD Ryzen 5 PRO 8540U",
                        "AMD Ryzen 7 PRO 7730U", "AMD Ryzen 5 7540U", "Intel Core i5-1350P", "AMD Ryzen 9 4900H",
                        "AMD Ryzen 9 4900HS", "AMD Ryzen 5 PRO 6650H", "AMD Ryzen 5 6600H", "Intel Core i5-1340P",
                        "Intel Core i5-12450HX", "Intel Core i5-13420H", "AMD Ryzen 7 7730U", "AMD Ryzen 7 4800H",
                        "AMD Ryzen 7 5800U", "AMD Ryzen 5 7535HS", "AMD Ryzen 7 5825U", "AMD Ryzen 7 4800HS",
                        "AMD Ryzen 5 6600HS Creator Edition", "AMD Ryzen 5 PRO 7540U", "Intel Xeon W-11855M",
                        "Intel Core Ultra 5 135U", "Intel Core Ultra 7 165U", "Intel Core Ultra 5 125U",
                        "AMD Ryzen 7 4980U Microsoft Surface Edition", "Intel Core i7-1270P", "Intel Core i5-1240P",
                        "Apple M1 Pro 8 Core", "Intel Core i5-12450H", "Intel Core i7-1260P", "AMD Ryzen 7 PRO 5850U",
                        "AMD Ryzen 5 5600H", "AMD Ryzen 5 Pro 7535U", "AMD Ryzen 7 Extreme Edition", "Intel Core i7-12700TE",
                        "AMD Ryzen 5 6600U", "AMD Ryzen 5 PRO 6650U", "AMD Ryzen 7 4800U", "Intel Core i7-11600H",
                        "Intel Core Ultra 7 155U", "Intel Core i7-10700E", "AMD Ryzen 7 PRO 5875U", "AMD Ryzen 5 7535U",
                        "Intel Core i5-11500H", "AMD Ryzen 5 7530U", "AMD Ryzen 7 4850U", "AMD Ryzen 7 5700U",
                        "Intel Core i5-11260H", "Intel Xeon W-10885M", "Intel Core i5-11400H", "Apple M2 8 Core",
                        "Intel Core i9-10980HK", "AMD Ryzen 5 5600U", "Intel Core i5-1345U", "AMD Ryzen 5 7430U",
                        "Intel Core 7 150U", "AMD Ryzen 7 PRO 4750U", "AMD Ryzen 5 5560U", "Intel Core i9-10885H",
                        "Intel Xeon E-2286M", "Intel Core i7-1355U", "AMD Ryzen 5 PRO 7530U", "AMD Ryzen 5 5625U",
                        "Intel Core i7-1365U", "AMD Ryzen 5 PRO 5650U", "Intel Core i7-10875H", "Intel Core i5-1335U",
                        "Intel Core Ultra 7 164U", "AMD Ryzen 5 PRO 4400GE", "Intel Core i9-10880H", "AMD Ryzen 5 PRO 5675U",
                        "Intel Core i5-12500TE", "Intel Core i7-10870H", "Intel Core i3-1220P", "AMD Ryzen 5 4600H",
                        "AMD Ryzen 5 4600HS", "Intel Core i7-1260U", "Apple M1 8 Core", "Intel Core i5-1334U",
                        "Intel Core i9-9980HK", "Intel Core i7-1265U", "Intel Core i5-1240U"));
            } else if ("student".equals(request.getOfficeType())) {
                tags.addAll(Arrays.asList("Intel Core i5-1240P", "AMD Ryzen 5 5600U", "Intel Core i5-1135G7", "Apple M1",
                        "Intel Core i3-1115G4", "AMD Ryzen 3 5300U", "Intel Celeron N4020", "AMD Athlon Silver 3050U"));
                tags.add("student");
            }
        }

        // 무게
        if ("heavy".equals(request.getWeight())) {
            tags.add("weight_over_2.5");
        } else if ("light".equals(request.getWeight())) {
            tags.add("weight_under_2.5");
        }

        // 화면 크기
        if ("large".equals(request.getScreen())) {
            tags.add("screen_size_15_plus"); //정보 들어오면 맞춰서 수정
        } else if ("medium".equals(request.getScreen())) {
            tags.add("screen_size_13_to_15");
        } else if ("small".equals(request.getScreen())) {
            tags.add("screen_size_under_13");
        }

        // 가격
        if ("budget".equals(request.getPrice())) {
            tags.add("price_under_1000000"); //가격 정보 들어오면 맞춰서 수정 대충 해놓은 것
        } else if ("performance".equals(request.getPrice())) {
            tags.add("price_over_2000000");
        } else if ("balanced".equals(request.getPrice())) {
            tags.add("price_1000000_to_2000000");
        }

        // 디자인
        if ("design".equals(request.getPriority())) {
            tags.addAll(Arrays.asList("Apple", "LG_Gram")); //이건 하드코딩 상황
        }

        // 동세대 최고 성능
        // 동세대 최고 성능
        if ("performance".equals(request.getPriority())) { //이것도 하드코딩
            tags.addAll(Arrays.asList("NVIDIA GeForce RTX 4090 Laptop GPU", "AMD Radeon RX 6950 XT",
                    "AMD Ryzen 9 7945HX", "AMD Ryzen 9 7940HX", "Intel Core i9-13980HX", "Intel Core i9-14900HX"));
        }

        // 우선순위에 따른 정렬 조건 추가 //우선 순위에 따라 정렬
        if ("usage".equals(request.getPriority())) {
            tags.add("sort_by_usage");
        } else if ("screen".equals(request.getPriority())) {
            tags.add("sort_by_screen_size");
        } else if ("weight".equals(request.getPriority())) {
            tags.add("sort_by_weight");
        } else {
            tags.add("sort_by_price");
        }

        return tags;
    }

    private LaptopDTO convertToDTO(LaptopDTO product) {
        LaptopDTO dto = new LaptopDTO();
        dto.setProductNo(product.getProductNo());
        dto.setTypeNo(product.getTypeNo());
        dto.setProductName(product.getProductName());
        dto.setPrice(product.getPrice());
        dto.setReferenceCode(product.getReferenceCode());
        dto.setWeight(product.getWeight());
        dto.setScreenSize(product.getScreenSize());
        dto.setBrand(product.getBrand());
        dto.setSeries(product.getSeries());
        return dto;
    }
}