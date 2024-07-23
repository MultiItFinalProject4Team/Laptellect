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

    @GetMapping("/recommend")
    public String recommendPage(Model model) {
        model.addAttribute("categories", laptopService.getCategories());
        model.addAttribute("cpuBrands", laptopService.getCpuBrands());
        model.addAttribute("gpuBrands", laptopService.getGpuBrands());
        model.addAttribute("priceRanges", laptopService.getPriceRanges());
        return "recommend";
    }

    @PostMapping("/getRecommendations")
    @ResponseBody
    public List<LaptopDTO> getRecommendations(@RequestBody Map<String, Object> surveyResults) {
        return laptopService.getRecommendedLaptops(surveyResults);
    }

    @GetMapping("/recommendResult")
    public String recommendResultPage() {
        return "recommendpage";
    }

    @GetMapping("/detail/{id}")
    @ResponseBody
    public Map<String, Object> getLaptopDetail(@PathVariable Long id) {
        return laptopService.getLaptopDetail(id);
    }
}