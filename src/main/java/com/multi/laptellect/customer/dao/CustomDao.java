package com.multi.laptellect.customer.dao;

import com.multi.laptellect.customer.dto.PersonalqAppDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CustomDao {
    int personalqApp(PersonalqAppDto appDto);
}
