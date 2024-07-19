package com.multi.laptellect.customer.controller;

import com.multi.laptellect.customer.dto.NoticeDto;
import com.multi.laptellect.customer.dto.NoticeListDto;
import com.multi.laptellect.customer.dto.PersonalqDto;
import com.multi.laptellect.customer.dto.PersonalqListDto;
import com.multi.laptellect.customer.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/customer/user")
public class CustomerController {

    @Autowired
    CustomerService customerService;

    @GetMapping("/customer_notice")
    public void customer_notice(Model model){
        List<NoticeListDto> notice = customerService.getNoticeList();
        model.addAttribute("notice",notice);
    }

    @GetMapping("/customer_personalq")
    public void customer_personalq(Model model){
        List<PersonalqListDto> list = customerService.getPersonalqList();
        model.addAttribute("list",list);
    }

    @GetMapping("/customer_chatbot")
    public void customer_chatbot(){
    }

    @GetMapping("/notice_detail/{noticeNo}")
    public String notice_detail(@PathVariable("noticeNo") int noticeNo, Model model) {
        System.out.println(noticeNo);
        NoticeDto notice = customerService.getnotice(noticeNo);
        model.addAttribute("notice",notice);
        return "/customer/user/notice_detail";
    }

    @GetMapping("/personalq_detail/{personalqNo}")
    public String personalq_detail(@PathVariable("personalqNo") int personalqNo, Model model){
        PersonalqDto personalqDto = customerService.getPersonalq(personalqNo);
        model.addAttribute("personalq",personalqDto);
        return"/customer/user/personalq_detail";
    }
}
