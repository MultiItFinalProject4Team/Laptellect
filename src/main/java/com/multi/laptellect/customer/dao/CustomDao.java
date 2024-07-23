package com.multi.laptellect.customer.dao;

import com.multi.laptellect.customer.dto.PersonalqAppDto;
import com.multi.laptellect.customer.dto.PersonalqDto;
import com.multi.laptellect.customer.dto.PersonalqListDto;
import com.multi.laptellect.customer.dto.personalqImageDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CustomDao {
    int personalqApp(PersonalqAppDto appDto);

    List<PersonalqListDto> getPersonalqList();

    PersonalqDto getPersonalqDetail(int personalqNo);

    String getpersonalqCode(int personalqNo);

    void inputPersonalqAppImage(personalqImageDto imageDto);

    String[] getPersonalqImage(String referenceCode);
}
