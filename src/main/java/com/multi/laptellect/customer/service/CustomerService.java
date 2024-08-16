package com.multi.laptellect.customer.service;

import com.multi.laptellect.common.model.Email;
import com.multi.laptellect.customer.dao.CustomDao;
import com.multi.laptellect.customer.dto.*;
import com.multi.laptellect.customer.dto.ImageDto;
import com.multi.laptellect.util.EmailUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
        return customDao.getNoticeList();
    }


    public NoticeListDto getnotice(int noticeNo) {
        return customDao.getnotice(noticeNo);
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
        List<PersonalqCategoryDto> category = customDao.getPersonalqCategory();
        PersonalqCategoryDto category_all  = new PersonalqCategoryDto();
        category_all.setPersonalqCategorycode("personalq_all");
        category_all.setPersonalqCategoryname("카테고리");
        category.add(0,category_all);
        return category;
    }

    public int updatePersonalq(PersonalqAppDto appDto) {
        return customDao.updatePersonalq(appDto);
    }

    public int deletePersonalq(int personalqNo, String code) {
        String path = System.getProperty("user.dir");
        List<String> urls = new ArrayList<>();
        Document document = Jsoup.parse(customDao.getPersonalqDetail(personalqNo).getContent());
        for (Element img : document.select("img")) {
            urls.add(img.attr("src"));
        }
        for(String url : urls){
            Path filePath = Paths.get(path, url);
            try {
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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

    public void deletePersonala(int personalqNo,String code) {
        String path = System.getProperty("user.dir");
        List<String> urls = new ArrayList<>();
        Document document = Jsoup.parse(customDao.getPersonala(personalqNo).getContent());
        for (Element img : document.select("img")) {
            urls.add(img.attr("src"));
        }
        for(String url : urls){
            Path filePath = Paths.get(path, url);
            try {
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        customDao.deletePersonala(personalqNo);
    }

    public List<ProuductqListDto> getProudctqList(int productNo) {
        return customDao.getProudctqList(productNo);
    }

    public List<ProductqCategoryDto> getProductqCategory() {
        List<ProductqCategoryDto> category = customDao.getProductqCategory();
        ProductqCategoryDto category_all  = new ProductqCategoryDto();
        category_all.setProductqCategorycode("productq_all");
        category_all.setProductqCategoryname("전체");
        category.add(0,category_all);
        return category;
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
        email.setMailContent("문의주신 상품문의 "+getProductq(answerDto.getProductqNo()).getTitle()+"에 관리자가 답변을 남겼습니다.\n 답변내용: \n"+contents);
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

    public int deleteProductq(int productqNo, String code) {
        String path = System.getProperty("user.dir");
        List<String> urls = new ArrayList<>();
        Document document = Jsoup.parse(customDao.getProductq(productqNo).getContent());
        for (Element img : document.select("img")) {
            urls.add(img.attr("src"));
        }
        for(String url : urls){
            Path filePath = Paths.get(path, url);
            try {
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return customDao.deleteProductq(productqNo);
    }

    public void updateProducta(ProductqAnswerDto answerDto) {
        customDao.updateProducta(answerDto);
    }

    public String getProductaCode(int productaNo) {
        return customDao.getProductaCode(productaNo);
    }

    public void deleteProducta(int productqNo, String code) {
        String path = System.getProperty("user.dir");
        List<String> urls = new ArrayList<>();
        Document document = Jsoup.parse(customDao.getProducta(productqNo).getContent());
        for (Element img : document.select("img")) {
            urls.add(img.attr("src"));
        }
        for(String url : urls){
            Path filePath = Paths.get(path, url);
            try {
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        customDao.deleteProducta(productqNo);
    }

    public List<PersonalqListDto> getPersonalqSearchList(PersonalqSearchDto searchDto) {
        return customDao.getPersonalqSearchList(searchDto);
    }

    public List<PersonalqListDto> getAllPersonalqList() {
        return customDao.getAllPersonalqList();
    }

    public List<ProuductqListDto> getProudctqSearchList(ProductSearchDto searchDto) {
        return customDao.getProudctqSearchList(searchDto);
    }

    public List<ProuductqListDto> getMyProudctqSearchList(ProductSearchDto searchDto) {
        return customDao.getMyProudctqSearchList(searchDto);
    }

    public List<PersonalqListDto> getAllPersonalqSearchList(PersonalqSearchDto searchDto) {
        return customDao.getAllPersonalqSearchList(searchDto);
    }
    public int inputImage(String originName, String fileName) {
        ImageDto imageDto=ImageDto.builder()
                .originName(originName)
                .uploadName(fileName)
                .build();
        customDao.inputImage(imageDto);
        return 1;
    }

    public void setImage(String code) {
        customDao.setImage(code);
    }

    public List<ProductqList> getAllProductqList(int productNo) {
        return customDao.getAllProductqList(productNo);
    }

    public List<ProductqList> getProductQuestionList(int productNo) {
        return customDao.getProductQuestionList(productNo);
    }

    public List<ProductqList> getProductOpinionList(int productNo) {
        return customDao.getProductOpinionList(productNo);
    }

    public List<ProductqList> getProductSearchList(ProductSearchDto searchDto) {
        return  customDao.getProductSearchList(searchDto);
    }

    public void noticeApp(NoticeListDto noticeListDto) {
        customDao.noticeApp(noticeListDto);
    }

    public void deleteNotice(int noticeNo) {
        customDao.deleteNotice(noticeNo);
    }

    public void updateNotice(NoticeListDto dto) {
        customDao.updateNotice(dto);
    }

    public List<NoticeListDto> getNoticeSearchList(NoticeSearchDto dto) {
        return customDao.getNoticeSearchList(dto);
    }

    public List<AdminProductqList> getAdminProductqList(ProductSearchDto searchDto) {
        return customDao.getAdminProductqList(searchDto);
    }

    public List<AdminProductqList> getAllProductList() {
        return customDao.getAllProductList();
    }
}
