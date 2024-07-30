package com.multi.laptellect.customer.controller;

import com.multi.laptellect.customer.dto.*;
import com.multi.laptellect.customer.service.CustomerService;
import com.multi.laptellect.customer.service.PaginationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/customer/user")
public class CustomerController {

    int memberNo=1;

    @Autowired
    private CustomerService customerService;
    @Autowired
    private PaginationService pagination;


    //공지사항 페이지(메인)
    @GetMapping({"/customer_notice",""})
    public String customer_notice(Model model){
        List<NoticeListDto> notice = customerService.getNoticeList();
        model.addAttribute("notice",notice);
        return "/customer/user/customer_notice";
    }
    //1:1문의 페이지
    @GetMapping("/customer_personalq")
    public void customer_personalq(Model model, @RequestParam(value = "page",defaultValue = "1") int page){
        List<PersonalqListDto> list = customerService.getPersonalqList(memberNo);
        int page_size=10;
        int adjustPage=page-1;
        List<PersonalqListDto> paginationList=pagination.paginate(list, adjustPage, page_size);
        int totalPages = (int) Math.ceil((double) list.size() / pagination.pageSize);
        model.addAttribute("list",paginationList);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
    }
    //챗봇 페이지
    @GetMapping("/customer_chatbot")
    public void customer_chatbot(){
    }
    //공지사항 상세조회
    @GetMapping("/notice_detail/{noticeNo}")
    public String notice_detail(@PathVariable("noticeNo") int noticeNo, Model model) {
        System.out.println(noticeNo);
        NoticeDto notice = customerService.getnotice(noticeNo);
        model.addAttribute("notice",notice);
        return "/customer/user/notice_detail";
    }
    //1:1문의 상세조회
    @GetMapping("/personalq_detail/{personalqNo}")
    public String personalq_detail(@PathVariable("personalqNo") int personalqNo, Model model){
        PersonalqDto personalqDto = customerService.getPersonalq(personalqNo);
        String[] imageList = customerService.getImage(personalqDto.getReferenceCode());
        model.addAttribute("personalq",personalqDto);
        model.addAttribute("imageList",imageList);
        System.out.println("질문: "+personalqDto);

        if(personalqDto.getAnswer().equals("Y")) {
            PersonalqAnswerDto answerDto = customerService.getPersonala(personalqNo);
            String[] imageList2 = customerService.getImage(answerDto.getReferenceCode());
            model.addAttribute("personala", answerDto);
            model.addAttribute("imageList2", imageList2);
            System.out.println("답변: "+answerDto);
        }
        return"/customer/user/personalq_detail";
    }
    //1:1문의 신청 페이지 이동
    @GetMapping("/personalq_app")
    public void personalq_app(Model model){
        List<PersonalqCategoryDto> category = customerService.getPersonalqCategory();
        System.out.println(category);
        model.addAttribute("category",category);
    }
    //1:1 문의 신청
    @PostMapping("/personalq_app")
    public String personalq_app(PersonalqAppDto appDto, @RequestParam("image[]") MultipartFile[] images){
        System.out.println(appDto);
        for(MultipartFile image : images){
            if(!image.isEmpty()){
                System.out.println(image.getOriginalFilename());
            }
        }
        appDto.setMemberNo(memberNo);
        int text_result=customerService.personalqApp(appDto);
        String code="personalq"+appDto.getPersonalqNo();
        customerService.setPersonalqCode(appDto.getPersonalqNo(),code);
        System.out.println(code);
        int image_result = customerService.inputImage(code,images);
        return "redirect:/customer/user/customer_personalq";
    }

    /**
     * 1:1문의 수정 페이지
     * @param model
     * @param personalqNo
     * @return
     */
    @GetMapping("/update_personalq/{personalqNo}")
    public String update_personalq(Model model, @PathVariable("personalqNo") int personalqNo){
        System.out.println(personalqNo);
        PersonalqDto dto = customerService.getPersonalq(personalqNo);
        List<PersonalqCategoryDto> categoryDto = customerService.getPersonalqCategory();
        model.addAttribute("category",categoryDto);
        model.addAttribute("dto",dto);
        return "/customer/user/personalq_update";
    }

    /**
     * 1:1문의 질문 수정
     * @param appDto
     * @param images
     * @return
     */
    @PostMapping("/update_personalq")
    public String update_personalq(PersonalqAppDto appDto, @RequestParam("image[]") MultipartFile[] images){
        System.out.println(appDto);
        for(MultipartFile image : images){
            if(!image.isEmpty()){
                System.out.println("이미지:"+image.getOriginalFilename());
            }
        }
        appDto.setMemberNo(memberNo);
        int text_result=customerService.updatePersonalq(appDto);
        String code = customerService.getpersonalqCode(appDto.getPersonalqNo());
        System.out.println(code);
        int image_result = customerService.updateImage(code,images);
        String redirectUrl = String.format("/customer/user/personalq_detail/%s", appDto.getPersonalqNo());
        return "redirect:"+redirectUrl;
    }

    //1:1 문의 질문 삭제
    @GetMapping("/delete_personalq/{personalqNo}")
    public String delete_personalq(@PathVariable("personalqNo") int personalqNo){
        int result = customerService.deletePersonalq(personalqNo);
        return "redirect:/customer/user/customer_personalq";
    }

    //임시 상품(1)
    @GetMapping("/product")
    public void product(){}

    //상품 문의 이동
    @GetMapping("/customer_productq/{productNo}")
    public String customer_productq(@PathVariable("productNo") int productNo, Model model){
        List<ProuductqListDto> productqList = customerService.getProudctqList(productNo);
        System.out.println(productNo);
        model.addAttribute("productqList",productqList);
        model.addAttribute("productNo",productNo);
        model.addAttribute("memberNo",memberNo);
        model.addAttribute("state","all");
        return "/customer/user/customer_productq";
    }
    //상품 문의 신청 이동
    @GetMapping("/productq_app")
    public void productq_app(Model model, @RequestParam("productNo") int productNo){
        List<ProductqCategoryDto> category = customerService.getProductqCategory();
        System.out.println(category);
        System.out.println(productNo);
        model.addAttribute("category",category);
        model.addAttribute("productNo",productNo);
    }
    /**
     * 상품 문의 신청 메서드
     *
     * @param appDto the ProductqAppDto
     * @return the String
     */
    @PostMapping("/productq_app")
    public String productq_app(ProductqAppDto appDto, @RequestParam("image[]") MultipartFile[] images){
        appDto.setMemberNo(memberNo);
        System.out.println(appDto);
        for(MultipartFile image : images){
            if(!image.isEmpty()){
                System.out.println(image.getOriginalFilename());
            }
        }
        int text_result=customerService.productqApp(appDto);
        String code="productq"+appDto.getProductqNo();
        customerService.setProductqCode(appDto.getProductqNo(),code);
        System.out.println(code);
        int image_result = customerService.inputImage(code,images);
        String redirectUrl = String.format("/customer/user/customer_productq/%s", appDto.getProductNo());
        return "redirect:"+redirectUrl;
    }

    /**
     * 상품 문의 상세보기 메서드
     *
     * @param productqNo the productqNo
     * @return the String
     */
    @GetMapping("/productq_detail/{productqNo}")
    public String productq_detail(@PathVariable("productqNo") int productqNo, Model model){
        ProductqDto productqDto = customerService.getProductq(productqNo);
        String[] imageList = customerService.getImage(productqDto.getReferenceCode());
        System.out.println(productqDto);
        model.addAttribute("productq",productqDto);
        model.addAttribute("imageList",imageList);

        if(productqDto.getAnswer().equals("Y")) {
            ProductqAnswerDto answerDto = customerService.getProducta(productqNo);
            String[] imageList2 = customerService.getImage(answerDto.getReferenceCode());
            model.addAttribute("producta", answerDto);
            model.addAttribute("imageList2", imageList2);
            System.out.println("답변: "+answerDto);
        }
        return"/customer/user/productq_detail";
    }

    /**
     * 내 문의 보기 메소드
     * @param productNo
     * @param model
     * @return
     */
    @GetMapping("/my_productq/{productNo}")
    public String my_productq(@PathVariable("productNo") int productNo, Model model){
        System.out.println(productNo);
        List<ProuductqListDto> productqList = customerService.getMyProudctqList(productNo, memberNo);
        model.addAttribute("productqList",productqList);
        model.addAttribute("productNo",productNo);
        model.addAttribute("memberNo",memberNo);
        model.addAttribute("state","my");
        return "/customer/user/customer_productq";
    }

    /**
     * 상품문의 수정 페이지
     * @param model
     * @param productqNo
     * @return
     */
    @GetMapping("/update_productq/{productqNo}")
    public String update_productq(Model model, @PathVariable("productqNo") int productqNo){
        ProductqDto dto = customerService.getProductq(productqNo);
        List<ProductqCategoryDto> categoryDto = customerService.getProductqCategory();
        model.addAttribute("category",categoryDto);
        model.addAttribute("dto",dto);
        return "/customer/user/productq_update";
    }

    /**
     * 상품 문의 수정 메소
     * @param appDto
     * @param images
     * @return
     */
    @PostMapping("/update_productq")
    public String update_productq(ProductqAppDto appDto, @RequestParam("image[]") MultipartFile[] images){
        System.out.println(appDto);
        for(MultipartFile image : images){
            if(!image.isEmpty()){
                System.out.println("이미지:"+image.getOriginalFilename());
            }
        }
        appDto.setMemberNo(memberNo);
        int text_result=customerService.updateProductq(appDto);
        String code = customerService.getproductqCode(appDto.getProductqNo());
        System.out.println(code);
        int image_result = customerService.updateImage(code,images);
        String redirectUrl = String.format("/customer/user/productq_detail/%s", appDto.getProductqNo());
        return "redirect:"+redirectUrl;
    }

}
