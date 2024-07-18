package com.multi.laptellect.test.model.dao;

import com.multi.laptellect.test.model.dto.TestDTO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface TestDAO {
    TestDTO selectTest();
}