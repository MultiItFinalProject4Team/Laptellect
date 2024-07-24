package com.multi.laptellect.customer.dao;

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

    void inputImage(personalqImageDto imageDto);

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
}
