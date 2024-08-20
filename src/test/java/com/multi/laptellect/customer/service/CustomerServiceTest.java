package com.multi.laptellect.customer.service;

import com.multi.laptellect.customer.controller.CustomerController;
import org.springframework.beans.factory.annotation.Autowired;

class CustomerServiceTest {
    @Autowired
    CustomerController customerController;

//    @Test
//    void getNotice() {
//        List<NoticeListDto> notice = new ArrayList<>();
//        NoticeListDto note = NoticeListDto.builder()
//                .no(1)
//                .title("title1")
//                .createdAt("2024-07-18")
//                .build();
//        notice.add(note);
//        NoticeListDto note2 = NoticeListDto.builder()
//                .no(2)
//                .title("title2")
//                .createdAt("2024-07-19")
//                .build();
//        notice.add(note2);
//        assertThat(notice.get(0).getTitle()).isEqualTo("title1");
//    }
//
//    @Test
//    void getnotice() {
//        NoticeDto noticeDto = NoticeDto.builder()
//                .noticeNo(1)
//                .title("test")
//                .writer("admin")
//                .createDate("2024-07-19")
//                .content("content")
//                .build();
//        assertThat(noticeDto.getTitle()).isEqualTo("test");
//    }
}