package com.multi.laptellect.customer.controller;

import com.multi.laptellect.customer.dto.*;
import com.multi.laptellect.customer.service.CustomerService;
import com.multi.laptellect.customer.service.PaginationService;
import com.multi.laptellect.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 고객센터 관리자 클래스
 *
 */
@Controller
@RequestMapping("/customer/admin")
@RequiredArgsConstructor
public class CustomerAdminController {
    @Autowired
    CustomerService customerService;
    @Autowired
    PaginationService pagination;

    /**
     * 답변 페이지 이동 메소드
     *
     * @param personalqNo
     * @param model
     * @return 답변 페이지
     */
    @GetMapping("/answer_personalq/{personalqNo}")
    public String move_answer_personalq(@PathVariable("personalqNo") int personalqNo, Model model){
        model.addAttribute("personalqNo",personalqNo);
        return"customer/admin/answer_personalq";
    }

    /**
     * 답변 전송 메소드
     *
     * @param answerDto
     * @return 기존 상세조회 페이지
     */
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

    /**
     * 답변 수정 페이지 이동 메소드
     * @param personalqNo
     * @param model
     * @return 답변 수정 페이지
     */
    @GetMapping("/update_personala/{personalqNo}")
    public String update_answer(@PathVariable("personalqNo") int personalqNo, Model model){
        PersonalqAnswerDto dto = customerService.getPersonala(personalqNo);
        model.addAttribute("dto",dto);
        return"customer/admin/update_personala";
    }

    /**
     * 답변 수정 전송 메소드
     * @param answerDto
     * @return 기존 상세조회 페이지
     */
    @PostMapping("/update_personala")
    public String update_answer(PersonalqAnswerDto answerDto){
        customerService.updatePersonala(answerDto);
        String code=customerService.getPersonalaCode(answerDto.getPersonalaNo());
        System.out.println("코드"+code);
        String redirectUrl = String.format("/customer/user/personalq_detail/%s", answerDto.getPersonalqNo());
        return "redirect:"+redirectUrl;
    }

    /**
     * 답변 삭제 메소드
     * @param personalqNo
     * @return 기존 상세조회 페이지
     */
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
     * 상품문의 답변페이지 이동 메소드
     * @param productqNo
     * @param model
     * @return 상품 문의 답변 페이지
     */
    @GetMapping("/answer_productq/{productqNo}")
    public String answer_producta(@PathVariable("productqNo") int productqNo, Model model){
        model.addAttribute("productqNo",productqNo);
        return"customer/admin/answer_productq";
    }

    /**
     * 상품 문의 답변 전송 메소드
     * @param answerDto
     * @return the int
     */
    @PostMapping("/answer_productq")
    @ResponseBody
    public int answer_productq(ProductqAnswerDto answerDto){
        System.out.println(answerDto);
        customerService.productAnwerApp(answerDto);
        String state = "Y";
        customerService.productAnwerChange(answerDto.getProductqNo(), state);
        String code = "producta"+answerDto.getProductaNo();
        customerService.setproductaCode(answerDto.getProductaNo(),code);
        return 1;
    }

    /**
     * 상품 문의 답변 전송 메소드
     *
     * @param answerDto
     * @return 상품 문의 내역 상세조회 페이지
     */
    @PostMapping("/answer_productQ")
    public String answer_productQ(ProductqAnswerDto answerDto){
        customerService.productAnwerApp(answerDto);
        String state = "Y";
        customerService.productAnwerChange(answerDto.getProductqNo(),state);
        String code = "producta"+answerDto.getProductaNo();
        customerService.setproductaCode(answerDto.getProductaNo(),code);
        String redirectUrl = String.format("/customer/user/productq_detail/%s", answerDto.getProductqNo());
        return "redirect:"+redirectUrl;
    }

    /**
     * 상품 문의 답변 수정 이동 메소드(페이지)
     * @param productqNo
     * @param model
     * @return 상품 문의 답변 수정 페이지
     */
    @GetMapping("/update_producta/{productqNo}")
    public String update_proudcta(@PathVariable("productqNo") int productqNo,Model model){
        ProductqAnswerDto dto = customerService.getProducta(productqNo);
        model.addAttribute("dto",dto);
        return"customer/admin/update_producta";
    }

    /**
     * 상품 문의 답변 수정 이동 메소드(모달)
     *
     * @param productqNo
     * @return ResponseEntity
     */
    @GetMapping("/update_producta")
    public ResponseEntity<ProductqAnswerDto> update_producta(@RequestParam("productqNo") int productqNo) {
        // 서비스 또는 데이터베이스에서 제품 세부정보를 가져옴
        ProductqAnswerDto dto = customerService.getProducta(productqNo);
        // 제품 정보를 JSON 형식으로 반환
        return ResponseEntity.ok(dto);
    }

    /**
     * 상품문의 답변 수정 메소드(모달)
     * @param appDto
     * @return the int
     */
    @PostMapping("/update_producta")
    @ResponseBody
    public int update_producta(ProductqAnswerDto appDto){
        int memberNo = 0;
        try {
            memberNo= SecurityUtil.getUserNo();
        }catch (Exception e){
            System.out.println("미로그인");
        }
        System.out.println("수정: "+ appDto);
        customerService.updateProducta(appDto);
        return 1;
    }
    /**
     * 상품문의 답변 수정 메소드(페이지)
     * @param appDto
     * @return 상품 문의 상세조회 페이지
     */
    @PostMapping("/update_productA")
    public String update_proudctA(ProductqAnswerDto appDto){
        customerService.updateProducta(appDto);
        String redirectUrl = String.format("/customer/user/productq_detail/%s", appDto.getProductqNo());
        return "redirect:"+redirectUrl;
    }

    /**
     * 상품 문의 답변 삭제(ajax)
     *
     * @param productqNo
     * @return the ResponseEntity
     */
    @PostMapping("/delete_producta")
    public ResponseEntity<?> delete_productA(@RequestParam("productqNo") int productqNo) {
        String code = customerService.getProducta(productqNo).getReferenceCode();
        customerService.deleteProducta(productqNo, code);
        String state="N";
        customerService.productAnwerChange(productqNo,state);
        return ResponseEntity.ok(1); // 200 OK와 함께 결과 반환
    }

    /**
     * 상품문의 답변 삭제(페이지)
     * @param productqNo
     * @return 상품 문의 상세조회 페이지
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
     * @return 1:1 문의 전체 조회 페이지
     */
    @GetMapping("/all_personal_question")
    public String all_personal_question(Model model, @RequestParam(value = "page",defaultValue = "1") int page){
        List<PersonalqListDto> list = customerService.getAllPersonalqList();
        PersonalqSearchDto searchDto = PersonalqSearchDto.builder().answer("A").keyword("").category("").date("recent").build();
        int page_size=10;
        int adjustPage=page-1;
        List<PersonalqListDto> paginationList=pagination.personalpaginate(list, adjustPage, page_size);
        int totalPages = (int) Math.ceil((double) list.size() / page_size);
        if(totalPages==0){totalPages=1;}
        List<PersonalqCategoryDto> category = customerService.getPersonalqCategory();
        model.addAttribute("dto",searchDto);
        model.addAttribute("list",paginationList);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("category",category);
        model.addAttribute("role","admin");
        model.addAttribute("state","all");
        return "admin/customer/admin_personalq";
    }

    /**
     * 1:1 문의 전체 검색
     * @param model
     * @param page
     * @param searchDto
     * @return 1:1 문의 전체 검색 결과
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
        return "customer/admin/search_all_personalq";
    }

    /**
     * 공지사항 조회 메소드
     *
     * @param model
     * @param page
     * @return 공지사항 관리자 페이지
     */
    @GetMapping("/admin_notice")
    public String admin_notice(Model model, @RequestParam(value = "page",defaultValue = "1") int page){
        List<NoticeListDto> list = customerService.getNoticeList();
        NoticeSearchDto searchDto = NoticeSearchDto.builder().mainRegist("A").keyword("").date("recent").build();
        int page_size=10;
        int adjustPage=page-1;
        int count=0;
        for(NoticeListDto dto: list){
            if (dto.getMainRegist().equals("Y")) {
                count++;
            }
        }
        List<NoticeListDto> paginationList=pagination.noticepaginate(list, adjustPage, page_size);
        int totalPages = (int) Math.ceil((double) list.size() / page_size);
        if(totalPages==0){totalPages=1;}
        model.addAttribute("list",paginationList);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("dto",searchDto);
        model.addAttribute("state","all");
        return "admin/customer/admin_notice";
    }

    /**
     * 공지사항 등록 페이지 이동 메소드
     *
     * @param model
     * @return 공지사항 등록 페이지
     */
    @GetMapping("/notice_app")
    public String notice_app(Model model){
        model.addAttribute("memberNo",SecurityUtil.getUserNo());
        return "admin/customer/notice_app";
    }

    /**
     * 공지사항 등록 메소드
     *
     * @param noticeListDto
     * @param model
     * @param page
     * @return 등록한 공지사항 상세조회 페이지
     */
    @PostMapping("/notice_app")
    public String notice_app(NoticeListDto noticeListDto, Model model, @RequestParam(value = "page",defaultValue = "1") int page){
        System.out.println(noticeListDto);
        customerService.noticeApp(noticeListDto);
        List<NoticeListDto> list = customerService.getNoticeList();
        int page_size=10;
        int adjustPage=page-1;
        List<NoticeListDto> paginationList=pagination.noticepaginate(list, adjustPage, page_size);
        int totalPages = (int) Math.ceil((double) list.size() / page_size);
        if(totalPages==0){totalPages=1;}
        model.addAttribute("list",paginationList);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        return "redirect:/customer/admin/admin_notice";
    }

    /**
     * 공지사항 상세조회 메소드
     *
     * @param noticeNo
     * @param model
     * @return 상세조회 페이지
     */
    @GetMapping("/admin_notice_detail/{noticeNo}")
    public String notice_detail(@PathVariable("noticeNo") int noticeNo, Model model) {
        System.out.println(noticeNo);
        NoticeListDto notice = customerService.getnotice(noticeNo);
        model.addAttribute("notice",notice);
        return "admin/customer/admin_notice_detail";
    }

    /**
     * 공지사항 삭제 메소드
     *
     * @param noticeNo
     * @return 전체조회 페이지
     */
    @GetMapping("/delete_notice/{noticeNo}")
    public String delete_notice(@PathVariable("noticeNo")int noticeNo){
        customerService.deleteNotice(noticeNo);
        return "redirect:/customer/admin/admin_notice";
    }

    /**
     * 공지사항 수정 페이지 이동 메소드
     *
     * @param noticeNo
     * @param model
     * @return 공지사항 수정 페이지
     */
    @GetMapping("/update_notice/{noticeNo}")
    public String update_notice(@PathVariable("noticeNo") int noticeNo, Model model){
        NoticeListDto dto = customerService.getnotice(noticeNo);
        model.addAttribute("dto",dto);
        return "admin/customer/notice_update";
    }

    /**
     * 공지사항 수정 메소드
     *
     * @param dto
     * @return 상세조회 페이지
     */
    @PostMapping("/update_notice")
    public String update_notice(NoticeListDto dto){
        customerService.updateNotice(dto);
        String redirectUrl = String.format("/customer/admin/admin_notice_detail/%s", dto.getNoticeNo());
        return "redirect:"+redirectUrl;
    }

    /**
     * 공지사항 검색 메소드
     * @param dto
     * @param model
     * @param page
     * @return 검색 결과 전체조회 페이지
     */
    @GetMapping("/search_notice")
    public String search_notice(NoticeSearchDto dto, Model model, @RequestParam(value = "page",defaultValue = "1") int page){
        System.out.println(dto);
        List<NoticeListDto> list = customerService.getNoticeSearchList(dto);
        int page_size=10;
        int adjustPage=page-1;
        List<NoticeListDto> paginationList=pagination.noticepaginate(list, adjustPage, page_size);
        int totalPages = (int) Math.ceil((double) list.size() / page_size);
        if(totalPages==0){totalPages=1;}
        model.addAttribute("dto",dto);
        model.addAttribute("list",paginationList);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("state","search");
        return "admin/customer/admin_notice";
    }

    /**
     * 고객센터 관리자 페이지 이동 메소드
     *
     * @return 고객센터 관리자 페이지
     */
    @GetMapping("/admin_customer")
    public String admin_customer(){
        return "admin/customer/admin_customer";
    }

    /**
     * 1:1 문의 전체 검색 메소드
     *
     * @param model
     * @param page
     * @param searchDto
     * @return 1:1 문의 관리자 페이지 검색 결과
     */
    @GetMapping("/search_all_personal_question")
    public String search_all_personal_question(Model model, @RequestParam(value = "page",defaultValue = "1") int page, PersonalqSearchDto searchDto){
        List<PersonalqListDto> list = customerService.getAllPersonalqSearchList(searchDto);
        int page_size=10;
        int adjustPage=page-1;
        List<PersonalqListDto> paginationList=pagination.personalpaginate(list, adjustPage, page_size);
        int totalPages = (int) Math.ceil((double) list.size() / page_size);
        if(totalPages==0){totalPages=1;}
        List<PersonalqCategoryDto> category = customerService.getPersonalqCategory();
        model.addAttribute("dto",searchDto);
        model.addAttribute("list",paginationList);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("category",category);
        model.addAttribute("role","admin");
        model.addAttribute("state","search");
        return "admin/customer/admin_personalq";
    }

    /**
     * 상품 문의 전체 조회 메소드
     *
     * @param model
     * @param page
     * @return 상품 문의 관리자 페이지
     */
    @GetMapping("/all_product_question")
    public String all_product_question(Model model, @RequestParam(value = "page",defaultValue = "1") int page){
        //List<AdminProductqList> list = customerService.getAdminProductqList(); 미답변 질문이 존재하는 리스트만
        List<AdminProductqList> list = customerService.getAllProductList();
        ProductSearchDto searchDto = ProductSearchDto.builder().keyword("").answer("A").date("recent").build();
        int page_size=10;
        int adjustPage=page-1;
        List<AdminProductqList> paginationList=pagination.productpaginate3(list, adjustPage, page_size);
        int totalPages = (int) Math.ceil((double) list.size() / page_size);
        if(totalPages==0){totalPages=1;}
        model.addAttribute("list",paginationList);
        model.addAttribute("dto",searchDto);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("state","all");
        return "admin/customer/admin_productqList";
    }

    /**
     * 상품 문의 관리자페이지 검색 메소드
     *
     * @param model
     * @param page
     * @param searchDto
     * @return 상품 문의 관리자 페이지 검색 결과
     */
    @GetMapping("search_all_product_question")
    public String search_all_product_question(Model model, @RequestParam(value = "page",defaultValue = "1") int page, ProductSearchDto  searchDto){
        List<AdminProductqList> list = customerService.getAdminProductqList(searchDto);
        int page_size=10;
        int adjustPage=page-1;
        List<AdminProductqList> paginationList=pagination.productpaginate3(list, adjustPage, page_size);
        int totalPages = (int) Math.ceil((double) list.size() / page_size);
        if(totalPages==0){totalPages=1;}
        model.addAttribute("list",paginationList);
        model.addAttribute("dto",searchDto);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("state","search");
        return "admin/customer/admin_productqList";
    }
}
