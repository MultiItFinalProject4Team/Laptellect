package com.multi.laptellect.customer.controller;

import com.multi.laptellect.customer.dto.PersonalqAnswerDto;
import com.multi.laptellect.customer.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/customer/admin")
@RequiredArgsConstructor
public class CustomerAdminController {
    @Autowired
    CustomerService customerService;
    @GetMapping("/answer_personalq/{personalqNo}")
    public String move_answer_personalq(@PathVariable("personalqNo") int personalqNo, Model model){
        model.addAttribute("personalqNo",personalqNo);
        return"/customer/admin/answer_personalq";
    }
    @PostMapping("/answer_personalq")
    public String answer_personalq(PersonalqAnswerDto answerDto, @RequestParam("image[]") MultipartFile[] images){
        System.out.println(answerDto);
        customerService.personalAnswerApp(answerDto);
        customerService.personalAnwerChange(answerDto.getPersonalqNo());
        String code=customerService.getPersonalaCode(answerDto.getPersonalaNo());
        customerService.inputImage(code,images);
        String redirectUrl = String.format("/customer/user/personalq_detail/%s", answerDto.getPersonalqNo());
        return "redirect:"+redirectUrl;
    }
}
