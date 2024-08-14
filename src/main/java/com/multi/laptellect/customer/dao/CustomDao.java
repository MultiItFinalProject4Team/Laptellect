package com.multi.laptellect.customer.dao;

import com.multi.laptellect.customer.dto.ImageDto;
import com.multi.laptellect.customer.dto.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CustomDao {
    int personalqApp(PersonalqAppDto appDto);

    List<PersonalqListDto> getPersonalqList(int memberNo);

    PersonalqDto getPersonalqDetail(int personalqNo);

    String getpersonalqCode(int personalqNo);

    void inputImage(ImageDto imageDto);

    String[] getImage(String referenceCode);

    void personalAnswerApp(PersonalqAnswerDto answerDto);

    String getPersonalaCode(int personalaNo);

    PersonalqAnswerDto getPersonala(int personalqNo);

    void personalAnwerChange(@Param("personalqNo")int personalqNo, @Param("state") String state);

    List<PersonalqCategoryDto> getPersonalqCategory();

    int updatePersonalq(PersonalqAppDto appDto);

    int deletePersonalq(int personalqNo);

    void deleteImages(String code);

    void updatePersonala(PersonalqAnswerDto answerDto);

    void setPersonalqCode(@Param("personalqNo") int personalqNo, @Param("code") String code);

    void setPersonalaCode(@Param("personalaNo") int personalaNo, @Param("code") String code);

    void deletePersonala(int personalqNo);

    List<ProuductqListDto> getProudctqList(int productNo);

    List<ProductqCategoryDto> getProductqCategory();

    int productqApp(ProductqAppDto appDto);

    void setProductqCode(@Param("productqNo") int productqNo, @Param("code") String code);

    ProductqDto getProductq(int productqNo);

    List<ProuductqListDto> getMyProudctqList(@Param("productNo") int productNo, @Param("memberNo") int memberNo);

    void productAnwerApp(ProductqAnswerDto answerDto);

    void productAnwerChange(@Param("productqNo") int productqNo, @Param("state") String state);

    void setproductaCode(@Param("productaNo") int productaNo, @Param("code") String code);

    ProductqAnswerDto getProducta(int productqNo);

    int updateProductq(ProductqAppDto appDto);

    String getproductqCode(int productqNo);

    int deleteProductq(int productqNo);

    void updateProducta(ProductqAnswerDto answerDto);

    String getProductaCode(int productaNo);

    void deleteProducta(int productqNo);

    List<PersonalqListDto> getPersonalqSearchList(PersonalqSearchDto searchDto);

    List<PersonalqListDto> getAllPersonalqList();

    List<ProuductqListDto> getProudctqSearchList(ProductSearchDto searchDto);

    List<ProuductqListDto> getMyProudctqSearchList(ProductSearchDto searchDto);

    List<PersonalqListDto> getAllPersonalqSearchList(PersonalqSearchDto searchDto);

    void setImage(String code);

    List<ProductqList> getAllProductqList(int productNo);

    List<ProductqList> getProductQuestionList(int productNo);

    List<ProductqList> getProductOpinionList(int productNo);

    List<ProductqList> getProductSearchList(ProductSearchDto searchDto);

    List<NoticeListDto> getNoticeList();

    NoticeListDto getnotice(int noticeNo);

    void noticeApp(NoticeListDto noticeListDto);
}
