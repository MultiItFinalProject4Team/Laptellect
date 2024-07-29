package com.multi.laptellect.payment.model.dao;

import com.multi.laptellect.payment.model.dto.*;
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

    int saveReview(PaymentReviewDTO paymentReviewDTO);

    List<String> getReviewedOrders();

    PaymentpointDTO selectpoint(String username);

    int usepoint(PaymentpointDTO paymentpointDTO);

    int givepoint(PaymentpointDTO paymentpointDTO);
}