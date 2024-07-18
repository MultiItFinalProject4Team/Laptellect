package com.multi.laptellect.test.service;

import com.multi.laptellect.test.model.dao.TestDAO;
import com.multi.laptellect.test.model.dto.TestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TestService {

    @Autowired
    private TestDAO testDao;

    public TestDTO selectTest() {
        return testDao.selectTest();
    }
}