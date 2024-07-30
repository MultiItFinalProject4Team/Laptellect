package com.multi.laptellect.customer.service;

import com.multi.laptellect.customer.dto.ImageDto;
import com.multi.laptellect.common.model.Email;
import com.multi.laptellect.customer.dao.CustomDao;
import com.multi.laptellect.customer.dto.*;
import com.multi.laptellect.util.EmailUtil;
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
    @Autowired
    private EmailUtil emailUtil;

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

    public List<PersonalqListDto> getPersonalqList(int memberNo) {
        List<PersonalqListDto> list = customDao.getPersonalqList(memberNo);
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

                ImageDto imageDto = ImageDto.builder()
                        .originName(fileName)
                        .uploadName(storeFileName)
                        .referenceCode(code)
                        .build();

                File directory = new File(path);
                if (!directory.exists()) {
                    directory.mkdirs();
                }

                try {
                    image.transferTo(new File(path+storeFileName));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                customDao.inputImage(imageDto);
            }
        }
        return 1;
    }

    public String[] getImage(String referenceCode) {
        return customDao.getImage(referenceCode);
    }

    public void personalAnswerApp(PersonalqAnswerDto answerDto) {
        customDao.personalAnswerApp(answerDto);
        String contents = answerDto.getContent();
        contents=contents.replaceAll("<[^>]*>", " ");
        Email email=new Email();
        email.setMailTitle("답변이 완료되었습니다");
        email.setMailContent("문의주신 "+getPersonalq(answerDto.getPersonalqNo()).getTitle()+"의 문의에 관리자가 답변을 남겼습니다.\n 답변내용: \n"+contents);
        email.setReceiveAddress("anjy0821@naver.com");

        try {
            emailUtil.sendEmail(email);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public String getPersonalaCode(int personalaNo) {
        return customDao.getPersonalaCode(personalaNo);
    }

    public PersonalqAnswerDto getPersonala(int personalqNo) {
        return customDao.getPersonala(personalqNo);
    }

    public void personalAnwerChange(int personalqNo, String state) {
        customDao.personalAnwerChange(personalqNo, state);
    }

    public List<PersonalqCategoryDto> getPersonalqCategory() {
        return customDao.getPersonalqCategory();
    }

    public int updatePersonalq(PersonalqAppDto appDto) {
        return customDao.updatePersonalq(appDto);
    }

    public int deletePersonalq(int personalqNo) {
        return customDao.deletePersonalq(personalqNo);
    }

    public int updateImage(String code, MultipartFile[] images) {
        for(MultipartFile image : images){
            if(!image.isEmpty()){
                customDao.deleteImages(code);
                break;
            }
        }
        for(MultipartFile image : images){
            if(!image.isEmpty()){
                String path = System.getProperty("user.dir")+"/uploads/";

                String fileName = image.getOriginalFilename();
                String uuid = UUID.randomUUID().toString();
                int extIndex = fileName.lastIndexOf(".") + 1;
                String ext = fileName.substring(extIndex);
                String storeFileName = uuid + "." + ext;

                ImageDto imageDto = ImageDto.builder()
                        .originName(fileName)
                        .uploadName(storeFileName)
                        .referenceCode(code)
                        .build();
                File directory = new File(path);
                if (!directory.exists()) {
                    directory.mkdirs();
                }

                try {
                    image.transferTo(new File(path+storeFileName));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                customDao.inputImage(imageDto);
            }
        }
        return 1;
    }

    public void updatePersonala(PersonalqAnswerDto answerDto) {
        customDao.updatePersonala(answerDto);
    }

    public void setPersonalqCode(int personalqNo, String code) {
        customDao.setPersonalqCode(personalqNo, code);
    }

    public void setPersonalaCode(int personalaNo, String code) {
        customDao.setPersonalaCode(personalaNo,code);
    }

    public void deletePersonala(int personalqNo) {
        customDao.deletePersonala(personalqNo);
    }

    public List<ProuductqListDto> getProudctqList(int productNo) {
        return customDao.getProudctqList(productNo);
    }

    public List<ProductqCategoryDto> getProductqCategory() {
        return customDao.getProductqCategory();
    }

    public int productqApp(ProductqAppDto appDto) {
        return customDao.productqApp(appDto);
    }

    public void setProductqCode(int productqNo, String code) {
        customDao.setProductqCode(productqNo, code);
    }

    public ProductqDto getProductq(int productqNo) {
        return customDao.getProductq(productqNo);
    }

    public List<ProuductqListDto> getMyProudctqList(int productNo, int memberNo) {
        return customDao.getMyProudctqList(productNo, memberNo);
    }

    public void productAnwerApp(ProductqAnswerDto answerDto) {
        customDao.productAnwerApp(answerDto);
        String contents = answerDto.getContent();
        contents=contents.replaceAll("<[^>]*>", " ");
        Email email=new Email();
        email.setMailTitle("답변이 완료되었습니다");
        email.setMailContent("문의주신 "+getProductq(answerDto.getProductqNo()).getTitle()+"의 문의에 관리자가 답변을 남겼습니다.\n 답변내용: \n"+contents);
        email.setReceiveAddress("anjy0821@naver.com");

        try {
            emailUtil.sendEmail(email);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void productAnwerChange(int productqNo, String state) {
        customDao.productAnwerChange(productqNo, state);
    }

    public void setproductaCode(int productaNo, String code) {
        customDao.setproductaCode(productaNo, code);
    }

    public ProductqAnswerDto getProducta(int productqNo) {
        return customDao.getProducta(productqNo);
    }

    public int updateProductq(ProductqAppDto appDto) {
        return customDao.updateProductq(appDto);
    }

    public String getproductqCode(int productqNo) {
        return customDao.getproductqCode(productqNo);
    }
}
