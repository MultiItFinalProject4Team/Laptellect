package com.multi.laptellect.customer.controller;

import com.multi.laptellect.customer.dto.*;
import com.multi.laptellect.customer.service.CustomerService;
import com.multi.laptellect.customer.service.PaginationService;
import com.multi.laptellect.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/customer/user")
public class CustomerController {

    @Autowired
    private CustomerService customerService;
    @Autowired
    private PaginationService pagination;

    //공지사항 페이지(메인)
    @GetMapping({"/customer_notice",""})
    public String customer_notice(Model model, @RequestParam(value = "page",defaultValue = "1") int page){
        List<NoticeListDto> list = customerService.getNoticeList();
        int page_size=10;
        int adjustPage=page-1;
        List<NoticeListDto> paginationList=pagination.noticepaginate(list, adjustPage, page_size);
        int totalPages = (int) Math.ceil((double) list.size() / page_size);
        if(totalPages==0){totalPages=1;}
        model.addAttribute("list",paginationList);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        return "/customer/user/customer_notice";
    }
    //1:1문의 페이지
    @GetMapping("/customer_personalq")
    public String customer_personalq(Model model, @RequestParam(value = "page",defaultValue = "1") int page){
        int memberNo;
        try {
            memberNo=SecurityUtil.getUserNo();
        }catch (Exception e){
            return "/auth/auth-sign-in";
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
        return "/customer/user/customer_personalq";
    }
    //챗봇 페이지
    @GetMapping("/customer_chatbot")
    public void customer_chatbot(){
    }
    //공지사항 상세조회
    @GetMapping("/notice_detail/{noticeNo}")
    public String notice_detail(@PathVariable("noticeNo") int noticeNo, Model model) {
        System.out.println(noticeNo);
        NoticeListDto notice = customerService.getnotice(noticeNo);
        model.addAttribute("notice",notice);
        return "/customer/user/notice_detail";
    }
    //1:1문의 상세조회
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
        return"/customer/user/personalq_detail";
    }
    //1:1문의 신청 페이지 이동
    @GetMapping("/personalq_app")
    public void personalq_app(Model model){
        List<PersonalqCategoryDto> category = customerService.getPersonalqCategory();
        category.remove(0);
        System.out.println(category);
        model.addAttribute("category",category);
    }
    //1:1 문의 신청
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
        categoryDto.remove(0);
        model.addAttribute("category",categoryDto);
        model.addAttribute("dto",dto);
        return "/customer/user/personalq_update";
    }

    /**
     * 1:1문의 질문 수정
     * @param appDto
     * @return
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

    //1:1 문의 질문 삭제
    @GetMapping("/delete_personalq/{personalqNo}")
    public String delete_personalq(@PathVariable("personalqNo") int personalqNo){
        String code = customerService.getPersonalq(personalqNo).getReferenceCode();
        int result = customerService.deletePersonalq(personalqNo, code);
        return "redirect:/customer/user/customer_personalq";
    }

    //임시 상품(1)
    @GetMapping("/my_productqList")
    public void product(Model model){
        List<Integer> plist = new ArrayList<>();
        for(int i=1; i<=5; i++){
            plist.add(i);
        }
        model.addAttribute("plist",plist);
    }

    //상품 문의 이동
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
        return "/customer/user/customer_productq";
    }
    //상품 문의 신청 이동
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
     * 상품 문의 신청 메서드
     *
     * @param appDto the ProductqAppDto
     * @return the String
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
    public String my_productq(@PathVariable("productNo") int productNo, Model model, @RequestParam(value = "page",defaultValue = "1") int page){
        int memberNo;
        try {
            memberNo=SecurityUtil.getUserNo();
        }catch (Exception e){
            return "/auth/auth-sign-in";
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
        categoryDto.remove(0);
        model.addAttribute("category",categoryDto);
        model.addAttribute("dto",dto);
        return "/customer/user/productq_update";
    }
    @GetMapping("/update_productq")
    public ResponseEntity<ProductqDto> getProductDetails(@RequestParam("productqNo") int productqNo) {
        // 서비스 또는 데이터베이스에서 제품 세부정보를 가져옴
        ProductqDto dto = customerService.getProductq(productqNo);

        // 제품 정보를 JSON 형식으로 반환
        return ResponseEntity.ok(dto);
    }

    /**
     * 상품 문의 수정 메소드
     * @param appDto
     * @return
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
     * 상품 문의 삭제
     * @param productqNo
     * @param productNo
     * @return
     */
    @GetMapping("/delete_productq/{productqNo}/{productNo}")
    public String delete_productq(@PathVariable("productqNo") int productqNo, @PathVariable("productNo") int productNo){
        String code = customerService.getProductq(productqNo).getReferenceCode();
        int result = customerService.deleteProductq(productqNo, code);
        String redirectUrl = String.format("/customer/user/customer_productq/%s", productNo);
        return "redirect:"+redirectUrl;
    }

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
     * 1:1 문의 검색
     * @param model
     * @param page
     * @return
     */
    @GetMapping("/search_personalq")
    public String search_personalq(Model model, @RequestParam(value = "page",defaultValue = "1") int page, PersonalqSearchDto searchDto){
        int memberNo;
        try {
            memberNo=SecurityUtil.getUserNo();
            searchDto.setMemberNo(memberNo);
        }catch (Exception e){
            return "/auth/auth-sign-in";
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
        return "/customer/user/search_personalq";
    }

    /**
     * 상품 문의 검색
     * @param model
     * @param productNo
     * @param page
     * @return
     */
    @GetMapping("/search_productq/{productNo}")
    public String search_productq(Model model, @RequestParam(value = "page",defaultValue = "1") int page,ProductSearchDto searchDto, @PathVariable("productNo") int productNo){
        int memberNo;
        try {
            memberNo=SecurityUtil.getUserNo();
            searchDto.setMemberNo(memberNo);
        }catch (Exception e){
            return "/auth/auth-sign-in";
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
        return "/customer/user/search_productq";
    }

    /**
     * 내 상품 문의 검색
     * @param model
     * @param productNo
     * @param page
     * @return
     */
    @GetMapping("/search_myproductq/{productNo}")
    public String search_myproductq(Model model, @PathVariable("productNo") int productNo, ProductSearchDto searchDto, @RequestParam(value = "page",defaultValue = "1") int page){
        int memberNo;
        try {
            memberNo=SecurityUtil.getUserNo();
            searchDto.setMemberNo(memberNo);
        }catch (Exception e){
            return "/auth/auth-sign-in";
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
        return "/customer/user/search_productq";
    }

    /**
     * ck에디터 사진 업로드
     * @param image
     * @return
     */
    @PostMapping("/imageUpload")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> imageUpload(@RequestParam("upload") MultipartFile image) {
        Map<String, Object> response = new HashMap<>();
        if (image.isEmpty()) {
            response.put("error", "No file uploaded");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        try {
            // 파일 확장자와 파일 이름 처리
            String uploadDir = System.getProperty("user.dir") + "/uploads/";
            String fileName = StringUtils.cleanPath(image.getOriginalFilename());
            String uuid = UUID.randomUUID().toString();
            String extension = fileName.substring(fileName.lastIndexOf("."));
            String storeFileName = uuid + extension;

            // 업로드 디렉토리 설정 및 파일 저장
            Path filePath = Paths.get(uploadDir, storeFileName);
            Files.createDirectories(filePath.getParent());
            image.transferTo(filePath.toFile());

            // 파일 접근 URL 반환
            String fileUrl = "/uploads/" + storeFileName;
            response.put("url", fileUrl);
            return ResponseEntity.ok(response);

        } catch (IOException e) {
            e.printStackTrace();
            response.put("error", "Failed to upload file");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    //파일 업로드 사용 예제
    //public void uploadFilesSample(
    //            @RequestPart(value = "files") List<MultipartFile> multipartFiles) {
    //        FileService.uploadFiles(multipartFiles, "customer");
    //    }

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
}
