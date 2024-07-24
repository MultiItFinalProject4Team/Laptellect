package com.multi.laptellect.recommend.laptop.controller;

import com.multi.laptellect.recommend.laptop.model.dto.LaptopDTO;
import com.multi.laptellect.recommend.laptop.service.LaptopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/laptop")
public class LaptopRecommendController {

    @Autowired
    private LaptopService laptopService;

    @GetMapping("/recommended")
    public String recommendPage(Model model) { //설문 페이지로 이동
        model.addAttribute("categories", laptopService.getCategories());
        model.addAttribute("cpuBrands", laptopService.getCpuBrands());
        model.addAttribute("gpuBrands", laptopService.getGpuBrands());
        model.addAttribute("priceRanges", laptopService.getPriceRanges()); //설문 페이지에 필요한 정보들을 model에 담아서 전달
        return "recommend/recommend"; //설문 페이지로 이동
    }

    @PostMapping("/getRecommendations")
    @ResponseBody
    public List<LaptopDTO> getRecommendations(@RequestBody Map<String, Object> surveyResults) { //설문 결과를 받아서 추천 노트북 목록을 반환
        return laptopService.getRecommendedLaptops(surveyResults);
    }

    @GetMapping("/recommendResult") //추천 결과 페이지로 이동
    public String recommendResultPage() {
        return "recommend/recommendpage";
    }

//    @GetMapping("/detail/{id}") //노트북 상세 정보인데 필요하면 풀것
//    @ResponseBody
//    public Map<String, Object> getLaptopDetail(@PathVariable Long id) {
//        return laptopService.getLaptopDetail(id);
//    }
}