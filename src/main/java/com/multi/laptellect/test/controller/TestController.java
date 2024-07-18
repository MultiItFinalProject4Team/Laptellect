package com.multi.laptellect.test.controller;

import com.multi.laptellect.test.model.dto.TestDTO;
import com.multi.laptellect.test.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/test")
public class TestController {

    @Autowired
    private TestService testService;

    @GetMapping("/test")
    public String selectTest(Model model) {
        TestDTO testDto = testService.selectTest();
        model.addAttribute("testDto", testDto);
        return "/payment/payment";  // payment.html 뷰를 반환
    }
}