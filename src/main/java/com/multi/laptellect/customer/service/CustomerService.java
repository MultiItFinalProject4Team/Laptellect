package com.multi.laptellect.customer.service;

import com.multi.laptellect.customer.dto.NoticeDto;
import com.multi.laptellect.customer.dto.NoticeListDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class CustomerService {

    public List<NoticeListDto> getNoticeList() {
        List<NoticeListDto> notice = new ArrayList<>();
        NoticeListDto note = NoticeListDto.builder()
                .no(1)
                .title("title1")
                .createdAt("2024-07-19")
                .build();
        notice.add(note);
        NoticeListDto note2 = NoticeListDto.builder()
                .no(2)
                .title("title2")
                .createdAt("2024-07-18")
                .build();
        notice.add(note2);
        return notice;
    }


    public NoticeDto getnotice(int noticeNo) {
        NoticeDto noticeDto = NoticeDto.builder()
                .build();
        return noticeDto;
    }
}
