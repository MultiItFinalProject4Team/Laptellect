package com.multi.laptellect.customer.controller;

import com.multi.laptellect.customer.dto.*;
import com.multi.laptellect.customer.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/customer/user")
public class CustomerController {

    @Autowired
    private CustomerService customerService;


    //공지사항 페이지(메인)
    @GetMapping({"/customer_notice",""})
    public String customer_notice(Model model){
        List<NoticeListDto> notice = customerService.getNoticeList();
        model.addAttribute("notice",notice);
        return "/customer/user/customer_notice";
    }
    //1:1문의 페이지
    @GetMapping("/customer_personalq")
    public void customer_personalq(Model model){
        List<PersonalqListDto> list = customerService.getPersonalqList();
        model.addAttribute("list",list);
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
        String[] imageList = customerService.getPersonalqImage(personalqDto.getReferenceCode());
        System.out.println(personalqDto);
        for(String image : imageList){
            System.out.println(image);
        }
        model.addAttribute("personalq",personalqDto);
        model.addAttribute("imageList",imageList);
        return"/customer/user/personalq_detail";
    }
    //1:1문의 신청 페이지 이동
    @GetMapping("/personalq_app")
    public void personalq_app(){}
    //1:1 문의 신청
    @PostMapping("/personalq_app")
    public String personalq_app(PersonalqAppDto appDto, @RequestParam("image[]") MultipartFile[] images){
        System.out.println(appDto);
        for(MultipartFile image : images){
            if(!image.isEmpty()){
                System.out.println(image.getOriginalFilename());
            }
        }
        appDto.setMemberNo(1);
        appDto.setProductqCategorycode("personalq_member");
        int text_result=customerService.personalqApp(appDto);
        String code = customerService.getpersonalqCode(appDto.getPersonalqNo());
        System.out.println(code);
        int image_result = customerService.inputPersonalqAppImage(code,images);
        return "redirect:/customer/user/customer_personalq";
    }

    //이미지 출력
    @GetMapping("/images/{image}")
    public ResponseEntity<Resource> downloadImage(@PathVariable("image") String fileName)  {
        String directory = System.getProperty("user.dir") + "/uploads/";
        String filePath = directory + fileName;
        File file = new File(filePath);

        Resource resource = new FileSystemResource(file); // 파일을 리소스로 변환


        // 파일 확장자를 통해 MIME 타입을 결정
        String mimeType;
        String ext = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        switch (ext) {
            case "jpg":
            case "jpeg":
                mimeType = "image/jpeg";
                break;
            case "png":
                mimeType = "image/png";
                break;
            case "gif":
                mimeType = "image/gif";
                break;
            default:
                mimeType = "application/octet-stream";  // 일반적인 바이너리 파일
        }

        // Content-Type 헤더를 설정하고 파일을 반환
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, mimeType);

        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
    }
}
