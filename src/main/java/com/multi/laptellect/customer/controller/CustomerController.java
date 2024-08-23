package com.multi.laptellect.customer.controller;

import com.multi.laptellect.common.model.FileDto;
import com.multi.laptellect.customer.dto.*;
import com.multi.laptellect.customer.service.CustomerService;
import com.multi.laptellect.customer.service.PaginationService;
import com.multi.laptellect.util.FileService;
import com.multi.laptellect.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 고객센터 유저 클래스
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/customer/user")
public class CustomerController {

    @Autowired
    private CustomerService customerService;
    @Autowired
    private PaginationService pagination;
    @Autowired
    private FileService fileService;

    /**
     * 공지사항 페이지(메인 페이지) 이동 메소드
     *
     * @param model
     * @param page
     * @return 공지사항 페이지
     */
    @GetMapping({"/customer_notice",""})
    public String customer_notice(Model model, @RequestParam(value = "page",defaultValue = "1") int page){
        NoticeSearchDto searchDto = NoticeSearchDto.builder().mainRegist("A").keyword("").date("recent").build();
        List<NoticeListDto> list = customerService.getNoticeList();
        int page_size=10;
        int adjustPage=page-1;
        List<NoticeListDto> paginationList=pagination.noticepaginate(list, adjustPage, page_size);
        int totalPages = (int) Math.ceil((double) list.size() / page_size);
        if(totalPages==0){totalPages=1;}
        model.addAttribute("list",paginationList);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("dto",searchDto);
        model.addAttribute("state","all");
        return "customer/user/customer_notice";
    }

    /**
     * 1:1 문의 이동 메소드
     *
     * @param model
     * @param page
     * @return 1:1 문의 페이지
     */
    @GetMapping("/customer_personalq")
    public String customer_personalq(Model model, @RequestParam(value = "page",defaultValue = "1") int page){
        int memberNo;
        try {
            memberNo=SecurityUtil.getUserNo();
        }catch (Exception e){
            return "auth/auth-sign-in";
        }
        List<PersonalqListDto> list = customerService.getPersonalqList(memberNo);
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
        model.addAttribute("role","user");
        model.addAttribute("answer","A");
        model.addAttribute("date","recent");
        return "customer/user/customer_personalq";
    }

    /**
     * 챗봇 페이지
     *
     */
    @GetMapping("/customer_chatbot")
    public void customer_chatbot(){
    }

    /**
     * 공지사항 상세조회 메소드
     * @param noticeNo
     * @param model
     * @return 공지사항 상세조회 페이지
     */
    @GetMapping("/notice_detail/{noticeNo}")
    public String notice_detail(@PathVariable("noticeNo") int noticeNo, Model model) {
        System.out.println(noticeNo);
        NoticeListDto notice = customerService.getnotice(noticeNo);
        model.addAttribute("notice",notice);
        return "customer/user/notice_detail";
    }

    /**
     * 1:1 문의 상세조회 메소드
     *
     * @param personalqNo
     * @param model
     * @return 1:1 문의 상세조회 페이지
     */
    @GetMapping("/personalq_detail/{personalqNo}")
    public String personalq_detail(@PathVariable("personalqNo") int personalqNo, Model model){
        PersonalqDto personalqDto = customerService.getPersonalq(personalqNo);
        model.addAttribute("personalq",personalqDto);
        System.out.println("질문: "+personalqDto);

        if(personalqDto.getAnswer().equals("Y")) {
            PersonalqAnswerDto answerDto = customerService.getPersonala(personalqNo);
            model.addAttribute("personala", answerDto);
            System.out.println("답변: "+answerDto);
        }
        return"customer/user/personalq_detail";
    }

    /**
     * 1:1 문의 신청 페이지 이동 메소드
     *
     * @param model
     */
    @GetMapping("/personalq_app")
    public void personalq_app(Model model){
        List<PersonalqCategoryDto> category = customerService.getPersonalqCategory();
        category.remove(0);
        System.out.println(category);
        model.addAttribute("category",category);
    }

    /**
     * 1:1 문의 신청 메소드
     *
     * @param appDto
     * @return 1:1 문의 전체조회 페이지
     */
    @PostMapping("/personalq_app")
    public String personalq_app(PersonalqAppDto appDto){
        int memberNo;
        try {
            memberNo=SecurityUtil.getUserNo();
        }catch (Exception e){
            return "/auth/auth-sign-in";
        }
        System.out.println(appDto);
        appDto.setMemberNo(memberNo);
        int text_result=customerService.personalqApp(appDto);
        String code="personalq"+appDto.getPersonalqNo();
        customerService.setPersonalqCode(appDto.getPersonalqNo(),code);
        customerService.setImage(code);
        return "redirect:/customer/user/customer_personalq";
    }

    /**
     * 1:1 문의 수정 페이지 이동 메소드
     *
     * @param model
     * @param personalqNo
     * @return 1:1 문의 수정 페이지 이동
     */
    @GetMapping("/update_personalq/{personalqNo}")
    public String update_personalq(Model model, @PathVariable("personalqNo") int personalqNo){
        System.out.println(personalqNo);
        PersonalqDto dto = customerService.getPersonalq(personalqNo);
        List<PersonalqCategoryDto> categoryDto = customerService.getPersonalqCategory();
        categoryDto.remove(0);
        model.addAttribute("category",categoryDto);
        model.addAttribute("dto",dto);
        return "customer/user/personalq_update";
    }

    /**
     * 1:1문의 질문 수정
     *
     * @param appDto
     * @return 1:1 문의 상세조회 페이지
     */
    @PostMapping("/update_personalq")
    public String update_personalq(PersonalqAppDto appDto){
        int memberNo;
        try {
            memberNo=SecurityUtil.getUserNo();
        }catch (Exception e){
            return "/auth/auth-sign-in";
        }
        System.out.println(appDto);
        appDto.setMemberNo(memberNo);
        int text_result=customerService.updatePersonalq(appDto);
        String code = customerService.getpersonalqCode(appDto.getPersonalqNo());
        customerService.setImage(code);
        String redirectUrl = String.format("/customer/user/personalq_detail/%s", appDto.getPersonalqNo());
        return "redirect:"+redirectUrl;
    }

    /**
     * 1:1 문의 삭제 메소드
     *
     * @param personalqNo
     * @return 1:1 문의 전체조회 페이지
     */
    @GetMapping("/delete_personalq/{personalqNo}")
    public String delete_personalq(@PathVariable("personalqNo") int personalqNo){
        String code = customerService.getPersonalq(personalqNo).getReferenceCode();
        int result = customerService.deletePersonalq(personalqNo, code);
        return "redirect:/customer/user/customer_personalq";
    }

    /**
     * 상품 문의 이동 메소드
     *
     * @param productNo
     * @param model
     * @param page
     * @return 상품 문의 전체조회 페이지
     */
    @GetMapping("/customer_productq/{productNo}")
    public String customer_productq(@PathVariable("productNo") int productNo, Model model, @RequestParam(value = "page",defaultValue = "1") int page){
        int memberNo;
        try {
            memberNo=SecurityUtil.getUserNo();
        }catch (Exception e){
            return "/auth/auth-sign-in";
        }
        List<ProuductqListDto> productqList = customerService.getProudctqList(productNo);
        List<ProductqCategoryDto> category = customerService.getProductqCategory();
        System.out.println(productNo);
        int page_size=10;
        int adjustPage=page-1;
        List<ProuductqListDto> paginationList=pagination.productpaginate(productqList, adjustPage, page_size);
        int totalPages = (int) Math.ceil((double) productqList.size() / page_size);
        if(totalPages==0){totalPages=1;}
        model.addAttribute("productqList",paginationList);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("productNo",productNo);
        model.addAttribute("memberNo",memberNo);
        model.addAttribute("category",category);
        model.addAttribute("state","all");
        model.addAttribute("date","recent");
        model.addAttribute("answer","A");
        return "customer/user/customer_productq";
    }

    /**
     * 상품 문의 신청 이동 메소드
     *
     * @param model
     * @param productNo
     */
    @GetMapping("/productq_app")
    public void productq_app(Model model, @RequestParam("productNo") int productNo){
        List<ProductqCategoryDto> category = customerService.getProductqCategory();
        category.remove(0);
        System.out.println(category);
        System.out.println(productNo);
        model.addAttribute("category",category);
        model.addAttribute("productNo",productNo);
    }

    /**
     * 상품 문의 신청 메서드(모달)
     *
     * @param appDto the ProductqAppDto
     * @return the int
     */
    @PostMapping("/productq_app")
    @ResponseBody // JSON 응답을 반환하기 위해 추가
    public int productq_app(ProductqAppDto appDto) {
        int memberNo;
        try {
            memberNo = SecurityUtil.getUserNo();
        } catch (Exception e) {
            return 0;
        }
        appDto.setMemberNo(memberNo);
        System.out.println(appDto);
        int text_result = customerService.productqApp(appDto);
        String code = "productq" + appDto.getProductqNo();
        customerService.setProductqCode(appDto.getProductqNo(), code);
        System.out.println(code);
        return 1;
    }

    /**
     * 상품 문의 상세조회 이동 메서드
     *
     * @param productqNo the productqNo
     * @return 상품 문의 상세조회 페이지
     */
    @GetMapping("/productq_detail/{productqNo}")
    public String productq_detail(@PathVariable("productqNo") int productqNo, Model model){
        int memberNo;
        try {
            memberNo=SecurityUtil.getUserNo();
        }catch (Exception e){
            return "auth/auth-sign-in";
        }
        ProductqDto productqDto = customerService.getProductq(productqNo);
        String[] imageList = customerService.getImage(productqDto.getReferenceCode());
        System.out.println(productqDto);
        model.addAttribute("productq",productqDto);
        model.addAttribute("imageList",imageList);
        model.addAttribute("memberNo",memberNo);

        if(productqDto.getAnswer().equals("Y")) {
            ProductqAnswerDto answerDto = customerService.getProducta(productqNo);
            String[] imageList2 = customerService.getImage(answerDto.getReferenceCode());
            model.addAttribute("producta", answerDto);
            model.addAttribute("imageList2", imageList2);
            System.out.println("답변: "+answerDto);
        }
        return"customer/user/productq_detail";
    }

    /**
     * 내 상품 문의 내역 이동 메소드
     *
     * @param productNo
     * @param model
     * @return 내 상품 문의 내역 페이지
     */
    @GetMapping("/my_productq/{productNo}")
    public String my_productq(@PathVariable("productNo") int productNo, Model model, @RequestParam(value = "page",defaultValue = "1") int page){
        int memberNo;
        try {
            memberNo=SecurityUtil.getUserNo();
        }catch (Exception e){
            return "auth/auth-sign-in";
        }
        System.out.println(productNo);
        List<ProuductqListDto> productqList = customerService.getMyProudctqList(productNo, memberNo);
        List<ProductqCategoryDto> category = customerService.getProductqCategory();
        int page_size=10;
        int adjustPage=page-1;
        List<ProuductqListDto> paginationList=pagination.productpaginate(productqList, adjustPage, page_size);
        int totalPages = (int) Math.ceil((double) productqList.size() / page_size);
        if(totalPages==0){totalPages=1;}
        model.addAttribute("productqList",paginationList);
        model.addAttribute("category",category);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("productNo",productNo);
        model.addAttribute("memberNo",memberNo);
        model.addAttribute("state","my");
        model.addAttribute("date","recent");
        model.addAttribute("answer","A");
        return "customer/user/customer_productq";
    }

    /**
     * 상품문의 수정 페이지 이동 메소드
     *
     * @param model
     * @param productqNo
     * @return 상품 문의 수정 페이지
     */
    @GetMapping("/update_productq/{productqNo}")
    public String update_productq(Model model, @PathVariable("productqNo") int productqNo){
        ProductqDto dto = customerService.getProductq(productqNo);
        List<ProductqCategoryDto> categoryDto = customerService.getProductqCategory();
        categoryDto.remove(0);
        model.addAttribute("category",categoryDto);
        model.addAttribute("dto",dto);
        return "customer/user/productq_update";
    }

    /**
     * 상품 문의 수정 페이지 이동 메소드(모달)
     *
     * @param productqNo
     * @return the ResponseEntity
     */
    @GetMapping("/update_productq")
    public ResponseEntity<ProductqDto> getProductDetails(@RequestParam("productqNo") int productqNo) {
        // 서비스 또는 데이터베이스에서 제품 세부정보를 가져옴
        ProductqDto dto = customerService.getProductq(productqNo);

        // 제품 정보를 JSON 형식으로 반환
        return ResponseEntity.ok(dto);
    }

    /**
     * 상품 문의 수정 메소드(모달)
     *
     * @param appDto
     * @return the int
     */
    @PostMapping("/update_productq")
    @ResponseBody
    public int update_productq(ProductqAppDto appDto){
        int memberNo = 0;
        try {
            memberNo=SecurityUtil.getUserNo();
        }catch (Exception e){
            System.out.println("미로그인");
        }
        System.out.println("수정: "+ appDto);
        appDto.setMemberNo(memberNo);
        int text_result=customerService.updateProductq(appDto);
        String code = customerService.getproductqCode(appDto.getProductqNo());
        System.out.println(code);
        return text_result;
    }

    /**
     * 상품 문의 수정 메소드(페이지)
     *
     * @param appDto
     * @return 상품문의 상세조회 페이지
     */
    @PostMapping("/update_productQ")
    public String update_productQ(ProductqAppDto appDto){
        int memberNo = 0;
        try {
            memberNo=SecurityUtil.getUserNo();
        }catch (Exception e){
            System.out.println("미로그인");
        }
        System.out.println("수정: "+ appDto);
        appDto.setMemberNo(memberNo);
        int text_result=customerService.updateProductq(appDto);
        String code = customerService.getproductqCode(appDto.getProductqNo());
        String redirectUrl = String.format("/customer/user/productq_detail/%s", appDto.getProductqNo());
        return "redirect:"+redirectUrl;
    }

    /**
     * 상품 문의 삭제 메소드(페이지)
     *
     * @param productqNo
     * @param productNo
     * @return 상품문의 내역 조회 페이지
     */
    @GetMapping("/delete_productq/{productqNo}/{productNo}")
    public String delete_productq(@PathVariable("productqNo") int productqNo, @PathVariable("productNo") int productNo){
        String code = customerService.getProductq(productqNo).getReferenceCode();
        int result = customerService.deleteProductq(productqNo, code);
        String redirectUrl = String.format("/customer/user/customer_productq/%s", productNo);
        return "redirect:"+redirectUrl;
    }

    /**
     * 상품 문의 삭제 메소드(모달)
     *
     * @param productqNo
     * @return the ResponseEntity
     */
    @PostMapping("/delete_productq")
    public ResponseEntity<?> delete_productq(@RequestParam("productqNo") int productqNo) {
        String code = customerService.getProductq(productqNo).getReferenceCode();
        int result = customerService.deleteProductq(productqNo, code);
        if (result > 0) {
            return ResponseEntity.ok(result); // 200 OK와 함께 결과 반환
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("삭제 실패"); // 500 Internal Server Error
        }
    }

    /**
     * 1:1 문의 검색 메소드
     *
     * @param model
     * @param page
     * @return 상품 문의 검색 결과 페이지
     */
    @GetMapping("/search_personalq")
    public String search_personalq(Model model, @RequestParam(value = "page",defaultValue = "1") int page, PersonalqSearchDto searchDto){
        int memberNo;
        try {
            memberNo=SecurityUtil.getUserNo();
            searchDto.setMemberNo(memberNo);
        }catch (Exception e){
            return "auth/auth-sign-in";
        }
        System.out.println(searchDto);
        List<PersonalqListDto> list = customerService.getPersonalqSearchList(searchDto);
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
        model.addAttribute("answer",searchDto.getAnswer());
        model.addAttribute("date",searchDto.getDate());
        return "customer/user/search_personalq";
    }

    /**
     * 상품 문의 검색 메소드
     *
     * @param model
     * @param productNo
     * @param page
     * @return 상품 문의 검색 결과 메소드
     */
    @GetMapping("/search_productq/{productNo}")
    public String search_productq(Model model, @RequestParam(value = "page",defaultValue = "1") int page,ProductSearchDto searchDto, @PathVariable("productNo") int productNo){
        int memberNo;
        try {
            memberNo=SecurityUtil.getUserNo();
            searchDto.setMemberNo(memberNo);
        }catch (Exception e){
            return "auth/auth-sign-in";
        }
        searchDto.setProductNo(productNo);
        List<ProuductqListDto> productqList = customerService.getProudctqSearchList(searchDto);
        List<ProductqCategoryDto> categories = customerService.getProductqCategory();
        int page_size=10;
        int adjustPage=page-1;
        List<ProuductqListDto> paginationList=pagination.productpaginate(productqList, adjustPage, page_size);
        int totalPages = (int) Math.ceil((double) productqList.size() / page_size);
        if(totalPages==0){totalPages=1;}
        model.addAttribute("productqList",paginationList);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("productNo",productNo);
        model.addAttribute("memberNo",memberNo);
        model.addAttribute("category",categories);
        model.addAttribute("page_category",searchDto.getCategory());
        model.addAttribute("page_keyword",searchDto.getKeyword());
        model.addAttribute("answer",searchDto.getAnswer());
        model.addAttribute("date",searchDto.getDate());
        model.addAttribute("state","all");
        return "customer/user/search_productq";
    }

    /**
     * 내 상품 문의 검색 메소드
     *
     * @param model
     * @param productNo
     * @param page
     * @return 상품 문의 페이지 검색 결과
     */
    @GetMapping("/search_myproductq/{productNo}")
    public String search_myproductq(Model model, @PathVariable("productNo") int productNo, ProductSearchDto searchDto, @RequestParam(value = "page",defaultValue = "1") int page){
        int memberNo;
        try {
            memberNo=SecurityUtil.getUserNo();
            searchDto.setMemberNo(memberNo);
        }catch (Exception e){
            return "auth/auth-sign-in";
        }
        System.out.println(productNo);
        searchDto.setProductNo(productNo);
        List<ProuductqListDto> productqList = customerService.getMyProudctqSearchList(searchDto);
        List<ProductqCategoryDto> categories = customerService.getProductqCategory();
        int page_size=10;
        int adjustPage=page-1;
        List<ProuductqListDto> paginationList=pagination.productpaginate(productqList, adjustPage, page_size);
        int totalPages = (int) Math.ceil((double) productqList.size() / page_size);
        if(totalPages==0){totalPages=1;}
        model.addAttribute("productqList",paginationList);
        model.addAttribute("category",categories);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("productNo",productNo);
        model.addAttribute("memberNo",memberNo);
        model.addAttribute("page_category",searchDto.getCategory());
        model.addAttribute("page_keyword",searchDto.getKeyword());
        model.addAttribute("answer",searchDto.getAnswer());
        model.addAttribute("date",searchDto.getDate());
        model.addAttribute("state","my");
        return "customer/user/search_productq";
    }

    /**
     * ckEditor 이미지 업로드 메소드
     *
     * @param image
     * @return the ResponseEntity
     */
    @PostMapping("/imageUpload")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> imageUpload(@RequestParam("upload") MultipartFile image) {
        Map<String, Object> response = new HashMap<>();
        if (image.isEmpty()) {
            response.put("error", "No file uploaded");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        FileDto dto = fileService.uploadFiles(image, "customer");
        System.out.println("url: "+dto.getUploadFileUrl());
        System.out.println("path: "+dto.getUploadFilePath());
        System.out.println("name: "+dto.getUploadFileName());
        //오브젝트 스토리지
        String fileUrl = dto.getUploadFileUrl();
        response.put("url",fileUrl);
        return ResponseEntity.ok(response);

    }

    /**
     * 상품 문의 내역 조회 메소드(ajax)
     *
     * @param productNo
     * @param page
     * @return 상품 문의 전체 내역
     */
    @GetMapping("/get_AllproductqList")
    public ResponseEntity<PageResponse<ProductqList>> getAllProductqList(@RequestParam("productNo") int productNo, @RequestParam("page") int page){
        try {
            int page_size=5;
            int adjustPage=page-1;
            List<ProductqList> productqList = customerService.getAllProductqList(productNo);
            List<ProductqList> paginationList=pagination.productpaginate2(productqList, adjustPage, page_size);
            int totalPages = (int) Math.ceil((double) productqList.size() / page_size);
            if(totalPages==0){totalPages=1;}

            PageResponse<ProductqList> response = new PageResponse<>(paginationList, totalPages);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // 예외 처리 로직 추가
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 상품 문의 문의 카테고리 조회 메소드(ajax)
     *
     * @param productNo
     * @param page
     * @return 상품 문의 문의 카테고리 데이터
     */

    @GetMapping("/getQuestion")
    public ResponseEntity<PageResponse<ProductqList>> getQuestion(@RequestParam("productNo") int productNo, @RequestParam("page") int page){
        try {
            int page_size=5;
            int adjustPage=page-1;
            List<ProductqList> productqList = customerService.getProductQuestionList(productNo);
            List<ProductqList> paginationList=pagination.productpaginate2(productqList, adjustPage, page_size);
            int totalPages = (int) Math.ceil((double) productqList.size() / page_size);
            if(totalPages==0){totalPages=1;}

            PageResponse<ProductqList> response = new PageResponse<>(paginationList, totalPages);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // 예외 처리 로직 추가
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 상품 문의 의견 카테고리 조회 메소드(ajax)
     *
     * @param productNo
     * @param page
     * @return 상품 문의 의견 카테고리 데이터
     */
    @GetMapping("/getOpinion")
    public ResponseEntity<PageResponse<ProductqList>> getOpinion(@RequestParam("productNo") int productNo, @RequestParam("page") int page){
        try {
            int page_size=5;
            int adjustPage=page-1;
            List<ProductqList> productqList = customerService.getProductOpinionList(productNo);
            List<ProductqList> paginationList=pagination.productpaginate2(productqList, adjustPage, page_size);
            int totalPages = (int) Math.ceil((double) productqList.size() / page_size);
            if(totalPages==0){totalPages=1;}

            PageResponse<ProductqList> response = new PageResponse<>(paginationList, totalPages);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // 예외 처리 로직 추가
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 상품 문의 검색 메소드(ajax)
     *
     * @param productNo
     * @param key
     * @param keyword
     * @param tpye
     * @param page
     * @return 상품 문의 검색 결과 데이터
     */
    @GetMapping("/getQuestionSearch")
    public ResponseEntity<PageResponse<ProductqList>> getQuestionSearch(@RequestParam("productNo") int productNo, @RequestParam("key") String key, @RequestParam("keyword") String keyword, @RequestParam("type") String tpye, @RequestParam("page") int page){
        ProductSearchDto searchDto = ProductSearchDto.builder()
                                        .productNo(productNo)
                                        .key(key)
                                        .keyword(keyword)
                                        .type(tpye)
                                        .build();
        System.out.println(searchDto);
        int page_size=5;
        int adjustPage=page-1;
        List<ProductqList> productqList = customerService.getProductSearchList(searchDto);
        List<ProductqList> paginationList=pagination.productpaginate2(productqList, adjustPage, page_size);
        int totalPages = (int) Math.ceil((double) productqList.size() / page_size);
        if(totalPages==0){totalPages=1;}
        PageResponse<ProductqList> response = new PageResponse<>(paginationList, totalPages);
        return ResponseEntity.ok(response);
    }

    /**
     * 상품 문의 답변 조회 메소드
     *
     * @param productqNo
     * @return 상품 문의 답변 데이터
     */
    @GetMapping("/get_AllproductaList")
    public ResponseEntity<ProductqAnswerDto> getAllProductaList(@RequestParam("productqNo") int productqNo){
        try {
            ProductqAnswerDto productaList = customerService.getProducta(productqNo);
            return ResponseEntity.ok(productaList);
        } catch (Exception e) {
            // 예외 처리 로직 추가
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 공지사항 검색 메소드
     *
     * @param dto
     * @param model
     * @param page
     * @return 공지사항 검색 결과 페이지
     */
    @GetMapping("/user_search_notice")
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
        return "customer/user/customer_notice";
    }

    /**
     * 내 상품 문의 내역 조회 메소드
     *
     * @param model
     * @param page
     * @return 내 상품 문의 내역 조회 페이지
     */
    @GetMapping("/user_productqList")
    public String user_proudctqList(Model model, @RequestParam(value = "page",defaultValue = "1") int page){
        ProductSearchDto searchDto = ProductSearchDto.builder().keyword("").date("recent").category("productq_all").answer("A").type("A").build();
        int memberNo;
        try {
            memberNo=SecurityUtil.getUserNo();
            searchDto.setMemberNo(memberNo);
        }catch (Exception e){
            return "auth/auth-sign-in";
        }
        List<UserProductqList> list = customerService.getUserProudctList(memberNo);
        int page_size=10;
        int adjustPage=page-1;
        List<UserProductqList> paginationList=pagination.productpaginate4(list, adjustPage, page_size);
        List<ProductqCategoryDto> category = customerService.getProductqCategory();
        int totalPages = (int) Math.ceil((double) list.size() / page_size);
        if(totalPages==0){totalPages=1;}
        model.addAttribute("list",paginationList);
        model.addAttribute("dto",searchDto);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("category",category);
        model.addAttribute("state","all");

        return "customer/user/user_productqList";
    }

    /**
     * 내 상품 문의 검색 메소드
     *
     * @param model
     * @param searchDto
     * @param page
     * @return 내 상품 문의 검색 결과 페이지
     */
    @GetMapping("search_user_productqList")
    public String search_user_productqList(Model model, ProductSearchDto searchDto, @RequestParam(value = "page",defaultValue = "1") int page){
        int memberNo;
        try {
            memberNo=SecurityUtil.getUserNo();
            searchDto.setMemberNo(memberNo);
        }catch (Exception e){
            return "auth/auth-sign-in";
        }
        List<UserProductqList> list = customerService.getUserSearchProudctList(searchDto);
        List<ProductqCategoryDto> category = customerService.getProductqCategory();
        int page_size=10;
        int adjustPage=page-1;
        List<UserProductqList> paginationList=pagination.productpaginate4(list, adjustPage, page_size);
        int totalPages = (int) Math.ceil((double) list.size() / page_size);
        if(totalPages==0){totalPages=1;}
        model.addAttribute("list",paginationList);
        model.addAttribute("dto",searchDto);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("category",category);
        model.addAttribute("state","search");
        return "customer/user/user_productqList";
    }
}
