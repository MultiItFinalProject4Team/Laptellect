package com.multi.laptellect.payment.model.dao;

import com.multi.laptellect.payment.model.dto.InsertDTO;
import com.multi.laptellect.payment.model.dto.OrderlistDTO;
import com.multi.laptellect.payment.model.dto.TestDTO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface TestDAO {

    TestDTO selectTest();

    int insertTest(InsertDTO insertDTO);

    List<OrderlistDTO> selectAllOrders();

    int updateRefundStatus(String ImpUid);
}