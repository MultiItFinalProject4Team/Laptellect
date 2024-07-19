package com.multi.laptellect.customer.service;

import com.multi.laptellect.customer.dto.NoticeDto;
import com.multi.laptellect.customer.dto.NoticeListDto;
import com.multi.laptellect.customer.dto.PersonalqDto;
import com.multi.laptellect.customer.dto.PersonalqListDto;
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
                .noticeNo(noticeNo)
                .title("test")
                .writer("admin")
                .createDate("2024-07-19")
                .content("content")
                .build();
        return noticeDto;
    }

    public List<PersonalqListDto> getPersonalqList() {
        List<PersonalqListDto> list = new ArrayList<>();
        PersonalqListDto dto = PersonalqListDto.builder()
                .no(1)
                .title("title1")
                .createdAt("2024-07-19")
                .build();
        list.add(dto);
        PersonalqListDto dto2 = PersonalqListDto.builder()
                .no(2)
                .title("title2")
                .createdAt("2024-07-18")
                .build();
        list.add(dto2);
        return list;
    }

    public PersonalqDto getPersonalq(int personalqNo) {
        PersonalqDto personalqDto = PersonalqDto.builder()
                .personalqNo(personalqNo)
                .title("title")
                .content("content")
                .writer("admin")
                .createDate("2024-07-19")
                .build();
        return personalqDto;
    }
}
