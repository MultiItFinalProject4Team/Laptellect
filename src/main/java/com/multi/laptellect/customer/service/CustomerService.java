package com.multi.laptellect.customer.service;

import com.multi.laptellect.customer.dao.CustomDao;
import com.multi.laptellect.customer.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerService {
    @Autowired
    private CustomDao customDao;

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
        List<PersonalqListDto> list = customDao.getPersonalqList();
        return list;
    }

    public PersonalqDto getPersonalq(int personalqNo) {
        PersonalqDto personalqDto = customDao.getPersonalqDetail(personalqNo);
        return personalqDto;
    }

    public int personalqApp(PersonalqAppDto appDto) {
        int result = customDao.personalqApp(appDto);
        return result;
    }
}
