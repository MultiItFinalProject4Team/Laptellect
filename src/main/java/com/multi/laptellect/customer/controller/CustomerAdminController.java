package com.multi.laptellect.customer.controller;

import com.multi.laptellect.customer.dto.PersonalqAnswerDto;
import com.multi.laptellect.customer.dto.ProductqAnswerDto;
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
    //답변 페이지 이동
    @GetMapping("/answer_personalq/{personalqNo}")
    public String move_answer_personalq(@PathVariable("personalqNo") int personalqNo, Model model){
        model.addAttribute("personalqNo",personalqNo);
        return"/customer/admin/answer_personalq";
    }
    //답변 전송
    @PostMapping("/answer_personalq")
    public String answer_personalq(PersonalqAnswerDto answerDto, @RequestParam("image[]") MultipartFile[] images){
        System.out.println(answerDto);
        customerService.personalAnswerApp(answerDto);
        String state="Y";
        customerService.personalAnwerChange(answerDto.getPersonalqNo(),state);
        String code="personala"+answerDto.getPersonalaNo();
        customerService.setPersonalaCode(answerDto.getPersonalaNo(), code);
        System.out.println("코드:"+code);
        customerService.inputImage(code,images);
        String redirectUrl = String.format("/customer/user/personalq_detail/%s", answerDto.getPersonalqNo());
        return "redirect:"+redirectUrl;
    }
    //답변 수정 페이지 이동
    @GetMapping("/update_personala/{personalqNo}")
    public String update_answer(@PathVariable("personalqNo") int personalqNo, Model model){
        PersonalqAnswerDto dto = customerService.getPersonala(personalqNo);
        model.addAttribute("dto",dto);
        return"/customer/admin/update_personala";
    }
    //답변 수정 전송
    @PostMapping("/update_personala")
    public String update_answer(PersonalqAnswerDto answerDto, @RequestParam("image[]") MultipartFile[] images){
        customerService.updatePersonala(answerDto);
        String code=customerService.getPersonalaCode(answerDto.getPersonalaNo());
        System.out.println("코드"+code);
        customerService.updateImage(code,images);
        String redirectUrl = String.format("/customer/user/personalq_detail/%s", answerDto.getPersonalqNo());
        return "redirect:"+redirectUrl;
    }

    //답변 삭제
    @GetMapping("/delete_personala/{personalqNo}")
    public String delete_answer(@PathVariable("personalqNo") int personalqNo){
        customerService.deletePersonala(personalqNo);
        String state="N";
        customerService.personalAnwerChange(personalqNo,state);
        String redirectUrl = String.format("/customer/user/personalq_detail/%s", personalqNo);
        return "redirect:"+redirectUrl;
    }

    /**
     * 상품문의 답변페이지 이동
     * @param productqNo
     * @param model
     * @return
     */
    @GetMapping("/answer_productq/{productqNo}")
    public String answer_producta(@PathVariable("productqNo") int productqNo, Model model){
        model.addAttribute("productqNo",productqNo);
        return"/customer/admin/answer_productq";
    }

    /**
     * 상품 문의 답변하기
     * @param answerDto
     * @param images
     * @return
     */
    @PostMapping("/answer_productq")
    public String answer_producta(ProductqAnswerDto answerDto, @RequestParam("image[]") MultipartFile[] images){
        System.out.println(answerDto);
        customerService.productAnwerApp(answerDto);
        String state = "Y";
        customerService.productAnwerChange(answerDto.getProductqNo(), state);
        String code = "producta"+answerDto.getProductaNo();
        customerService.setproductaCode(answerDto.getProductaNo(),code);
        customerService.inputImage(code,images);
        String redirectUrl = String.format("/customer/user/productq_detail/%s", answerDto.getProductqNo());
        return "redirect:"+redirectUrl;
    }
}
