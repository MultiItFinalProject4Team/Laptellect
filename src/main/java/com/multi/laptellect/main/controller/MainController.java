package com.multi.laptellect.main.controller;

import com.multi.laptellect.main.model.dto.ProductMainDTO;
import com.multi.laptellect.main.service.MainService;
import com.multi.laptellect.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;

@Controller
@Slf4j
@RequiredArgsConstructor
public class MainController {
    private final RedisUtil redisUtil;
    private final MainService mainService;

    @GetMapping("/")
    public String main(Model model) {
        try {
            ArrayList<ProductMainDTO> laptops = mainService.findProduct(1);
            ArrayList<ProductMainDTO> mouses = mainService.findProduct(2);
            ArrayList<ProductMainDTO> keyboards = mainService.findProduct(3);

            model.addAttribute("laptops", laptops);
            model.addAttribute("mouses", mouses);
            model.addAttribute("keyboards", keyboards);
        } catch (Exception e) {
            log.warn("메인 페이지 상품 리스트 조회 실패");
        }

        return "common/main";
    }
}