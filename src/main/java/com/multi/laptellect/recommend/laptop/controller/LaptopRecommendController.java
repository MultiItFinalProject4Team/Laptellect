package com.multi.laptellect.recommend.laptop.controller;


import com.multi.laptellect.recommend.laptop.model.dto.LaptopDTO;
import com.multi.laptellect.recommend.laptop.service.LaptopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class LaptopRecommendController {

    @Autowired
    private LaptopService laptopService;

    @GetMapping("/recommend")
            public String recomendPage() {
        return "recommend";
    }

    @PostMapping("/getRecommendations")
    public String getRecommendations(@RequestParam List<String> tags, Model model) {
        List<LaptopDTO> recommendations = laptopService.getRecommendedLaptops(tags);
        model.addAttribute("recommendations", recommendations);
        return "recommend :: laptopList";

    }


}
