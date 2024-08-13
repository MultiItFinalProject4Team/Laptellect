package com.multi.laptellect.customer.controller;

import com.multi.laptellect.customer.dto.*;
import com.multi.laptellect.customer.service.CustomerService;
import com.multi.laptellect.customer.service.PaginationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/customer/admin")
@RequiredArgsConstructor
public class CustomerAdminController {
    @Autowired
    CustomerService customerService;
    @Autowired
    PaginationService pagination;
    //답변 페이지 이동
    @GetMapping("/answer_personalq/{personalqNo}")
    public String move_answer_personalq(@PathVariable("personalqNo") int personalqNo, Model model){
        model.addAttribute("personalqNo",personalqNo);
        return"/customer/admin/answer_personalq";
    }
    //답변 전송
    @PostMapping("/answer_personalq")
    public String answer_personalq(PersonalqAnswerDto answerDto){
        System.out.println(answerDto);
        customerService.personalAnswerApp(answerDto);
        String state="Y";
        customerService.personalAnwerChange(answerDto.getPersonalqNo(),state);
        String code="personala"+answerDto.getPersonalaNo();
        customerService.setPersonalaCode(answerDto.getPersonalaNo(), code);
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
    public String update_answer(PersonalqAnswerDto answerDto){
        customerService.updatePersonala(answerDto);
        String code=customerService.getPersonalaCode(answerDto.getPersonalaNo());
        System.out.println("코드"+code);
        String redirectUrl = String.format("/customer/user/personalq_detail/%s", answerDto.getPersonalqNo());
        return "redirect:"+redirectUrl;
    }

    //답변 삭제
    @GetMapping("/delete_personala/{personalqNo}")
    public String delete_answer(@PathVariable("personalqNo") int personalqNo){
        String code = customerService.getPersonala(personalqNo).getReferenceCode();
        customerService.deletePersonala(personalqNo, code);
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
     * @return
     */
    @PostMapping("/answer_productq")
    @ResponseBody
    public int answer_producta(ProductqAnswerDto answerDto){
        System.out.println(answerDto);
        customerService.productAnwerApp(answerDto);
        String state = "Y";
        customerService.productAnwerChange(answerDto.getProductqNo(), state);
        String code = "producta"+answerDto.getProductaNo();
        customerService.setproductaCode(answerDto.getProductaNo(),code);
        return 1;
    }

    /**
     * 상품 문의 답변 수정 페이지
     * @param productqNo
     * @param model
     * @return
     */
    @GetMapping("/update_producta/{productqNo}")
    public String update_proudcta(@PathVariable("productqNo") int productqNo,Model model){
        ProductqAnswerDto dto = customerService.getProducta(productqNo);
        model.addAttribute("dto",dto);
        return"/customer/admin/update_producta";
    }

    /**
     * 상품문의 답변 수정 메소드
     * @param answerDto
     * @return
     */
    @PostMapping("/update_producta")
    public String update_producta(ProductqAnswerDto answerDto){
        customerService.updateProducta(answerDto);
        String code=customerService.getProductaCode(answerDto.getProductaNo());
        System.out.println("코드"+code);
        String redirectUrl = String.format("/customer/user/productq_detail/%s", answerDto.getProductqNo());
        return "redirect:"+redirectUrl;
    }

    /**
     * 상품문의 답변 삭제
     * @param productqNo
     * @return
     */
    @GetMapping("/delete_producta/{productqNo}")
    public String delete_producta(@PathVariable("productqNo") int productqNo){
        String code = customerService.getProducta(productqNo).getReferenceCode();
        customerService.deleteProducta(productqNo, code);
        String state="N";
        customerService.productAnwerChange(productqNo,state);
        String redirectUrl = String.format("/customer/user/productq_detail/%s", productqNo);
        return "redirect:"+redirectUrl;
    }

    /**
     * 관리자 페이지 1:1 문의 전체 조회
     * @param model
     * @param page
     * @return
     */
    @GetMapping("/all_personal_question")
    public String all_personal_question(Model model, @RequestParam(value = "page",defaultValue = "1") int page){
        List<PersonalqListDto> list = customerService.getAllPersonalqList();
        int page_size=10;
        int adjustPage=page-1;
        List<PersonalqListDto> paginationList=pagination.personalpaginate(list, adjustPage, page_size);
        int totalPages = (int) Math.ceil((double) list.size() / page_size);
        if(totalPages==0){totalPages=1;}
        List<PersonalqCategoryDto> category = customerService.getPersonalqCategory();
        model.addAttribute("list",paginationList);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("category",category);
        model.addAttribute("role","admin");
        return "/customer/user/customer_personalq";
    }

    /**
     * 1:1 문의 전체 검색
     * @param model
     * @param page
     * @param searchDto
     * @return
     */
    @GetMapping("/search_all_personalq")
    public String search_all_personalq(Model model, @RequestParam(value = "page",defaultValue = "1") int page, PersonalqSearchDto searchDto){
        List<PersonalqListDto> list = customerService.getAllPersonalqSearchList(searchDto);
        int page_size=10;
        int adjustPage=page-1;
        List<PersonalqListDto> paginationList=pagination.personalpaginate(list, adjustPage, page_size);
        int totalPages = (int) Math.ceil((double) list.size() / page_size);
        if(totalPages==0){totalPages=1;}
        List<PersonalqCategoryDto> categories = customerService.getPersonalqCategory();
        model.addAttribute("list",paginationList);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("category",categories);
        model.addAttribute("page_category",searchDto.getCategory());
        model.addAttribute("page_keyword",searchDto.getKeyword());
        model.addAttribute("role","admin");
        model.addAttribute("answer",searchDto.getAnswer());
        model.addAttribute("date",searchDto.getDate());
        return "/customer/admin/search_all_personalq";
    }
}
