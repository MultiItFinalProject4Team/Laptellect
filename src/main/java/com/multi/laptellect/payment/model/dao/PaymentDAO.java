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

    List<OrderlistDTO> selectOrders();

    int updateRefundStatus(String im_port_id);

    int saveReview(PaymentReviewDTO paymentReviewDTO);

    List<String> getReviewedOrders();

    PaymentpointDTO selectpoint(String username);

    int usepoint(PaymentpointDTO paymentpointDTO);

    int givepoint(PaymentpointDTO paymentpointDTO);


    PaymentpointDTO select_refundpoint(String im_port_id);


    int refundpoint(PaymentpointDTO paymentpointDTO);

}