package com.multi.laptellect.customer.dao;

import com.multi.laptellect.customer.dto.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CustomDao {
    int personalqApp(PersonalqAppDto appDto);

    List<PersonalqListDto> getPersonalqList();

    PersonalqDto getPersonalqDetail(int personalqNo);

    String getpersonalqCode(int personalqNo);

    void inputImage(personalqImageDto imageDto);

    String[] getImage(String referenceCode);

    void personalAnswerApp(PersonalqAnswerDto answerDto);

    String getPersonalaCode(int personalaNo);

    PersonalqAnswerDto getPersonala(int personalqNo);

    void personalAnwerChange(int personalqNo);
}
