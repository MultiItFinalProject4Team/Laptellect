package com.multi.laptellect.payment.service;

import com.multi.laptellect.payment.model.dao.TestDAO;
import com.multi.laptellect.payment.model.dto.InsertDTO;
import com.multi.laptellect.payment.model.dto.OrderlistDTO;
import com.multi.laptellect.payment.model.dto.TestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TestService {

    @Autowired
    private TestDAO testDao;

    @Transactional
    public TestDTO selectTest() {
        return testDao.selectTest();
    }

    @Transactional
    public int insertTest(InsertDTO insertDTO) {
        return testDao.insertTest(insertDTO);
    }

    @Transactional
    public List<OrderlistDTO> selectAllOrders() {
        return testDao.selectAllOrders();
    }

    @Transactional
    public int updateRefundStatus(String ImpUid) {
        return testDao.updateRefundStatus(ImpUid);
    }
}