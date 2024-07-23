package com.multi.laptellect.customer.service;

import com.multi.laptellect.customer.dao.CustomDao;
import com.multi.laptellect.customer.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

    public String getpersonalqCode(int personalqNo) {
        return customDao.getpersonalqCode(personalqNo);
    }


    public int inputImage(String code, MultipartFile[] images) {
        for(MultipartFile image : images){
            if(!image.isEmpty()){
                String path = System.getProperty("user.dir")+"/uploads/";

                String fileName = image.getOriginalFilename();
                String uuid = UUID.randomUUID().toString();
                int extIndex = fileName.lastIndexOf(".") + 1;
                String ext = fileName.substring(extIndex);
                String storeFileName = uuid + "." + ext;

                personalqImageDto imageDto = personalqImageDto.builder()
                        .originName(fileName)
                        .uploadName(storeFileName)
                        .referenceCode(code)
                        .build();

                customDao.inputImage(imageDto);

                File directory = new File(path);
                if (!directory.exists()) {
                    directory.mkdirs();
                }

                try {
                    image.transferTo(new File(path+storeFileName));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return 1;
    }

    public String[] getImage(String referenceCode) {
        return customDao.getImage(referenceCode);
    }

    public void personalAnswerApp(PersonalqAnswerDto answerDto) {
        customDao.personalAnswerApp(answerDto);
    }

    public String getPersonalaCode(int personalaNo) {
        return customDao.getPersonalaCode(personalaNo);
    }

    public PersonalqAnswerDto getPersonala(int personalqNo) {
        return customDao.getPersonala(personalqNo);
    }

    public void personalAnwerChange(int personalqNo) {
        customDao.personalAnwerChange(personalqNo);
    }
}
