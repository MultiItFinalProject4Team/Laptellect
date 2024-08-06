package com.multi.laptellect.payment.model.dao;

import com.multi.laptellect.payment.model.dto.*;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface PaymentDAO {

    PaymentpageDTO selectpaymentpage();

    int insertPayment(PaymentDTO paymentDTO);

    List<OrderlistDTO> selectOrders(String memberName);

    int updateRefundStatus(String imPortId);

    int saveReview(PaymentReviewDTO paymentReviewDTO);

    List<String> getReviewedOrders();

    PaymentpointDTO selectpoint(int memberNO);

    int usepoint(PaymentpointDTO paymentpointDTO);

    int givepoint(PaymentpointDTO paymentpointDTO);


    PaymentpointDTO select_refundpoint(String imPortId);


    int refundpoint(PaymentpointDTO paymentpointDTO);

    int newMemberPoint(PaymentpointDTO paymentpointDTO);

    boolean findReview(String imPortId);

    int findReviewedPoint(String imPortId);

    int refundReviewdPoint(PaymentpointDTO paymentpointDTO);

    PaymentpageDTO findProduct(String productName);
}