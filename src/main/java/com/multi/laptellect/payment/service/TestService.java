package com.multi.laptellect.payment.service;

import com.multi.laptellect.payment.model.dao.TestDAO;
import com.multi.laptellect.payment.model.dto.InsertDTO;
import com.multi.laptellect.payment.model.dto.TestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TestService {

    @Autowired
    private TestDAO testDao;

    public TestDTO selectTest() {
        return testDao.selectTest();
    }

    @Transactional
    public int insertTest(InsertDTO insertDTO) {
        return testDao.insertTest(insertDTO);
    }
}