package com.multi.laptellect.payment.model.dao;

import com.multi.laptellect.common.model.PaginationDTO;
import com.multi.laptellect.payment.model.dto.PaymentDTO;
import com.multi.laptellect.payment.model.dto.PaymentReviewDTO;
import com.multi.laptellect.payment.model.dto.PaymentpageDTO;
import com.multi.laptellect.payment.model.dto.PaymentpointDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Mapper
@Repository
public interface PaymentDAO {

    PaymentpageDTO selectpaymentpage();

    int insertPayment(PaymentDTO paymentDTO);

    List<PaymentDTO> selectOrders(String memberName);

    int updateRefundStatus(@Param("imPortId") String imPortId, @Param("paymentNo") Long paymentNo);


    int saveReview(PaymentReviewDTO paymentReviewDTO);

    List<String> getReviewedOrders();

    PaymentpointDTO selectpoint(int memberNO);

    int usepoint(PaymentpointDTO paymentpointDTO);

    int givepoint(PaymentpointDTO paymentpointDTO);


    PaymentpointDTO select_refundpoint(String imPortId);


    int refundPoint(PaymentpointDTO paymentpointDTO);

    int newMemberPoint(PaymentpointDTO paymentpointDTO);

    boolean findReview(String imPortId);

    int findReviewedPoint(String imPortId);

    int refundReviewdPoint(PaymentpointDTO paymentpointDTO);

    PaymentpageDTO findProduct(String productName);

    PaymentDTO findPaymentByImPortId(String imPortId);


    List<PaymentDTO> selectOrderItems(int memberNo);

    List<PaymentDTO> findPaymentsByImPortId(String imPortId);

    ArrayList<PaymentDTO> findAllPaymentByMemberNo(@Param("pageable") Pageable pageable,  @Param("paginationDTO") PaginationDTO paginationDTO, @Param("memberNo") int memberNo);

    int countPaymentByMemberNo(@Param("paginationDTO") PaginationDTO paginationDTO, @Param("memberNo")  int memberNo);

    @Select("SELECT * FROM payment WHERE payment_no = #{ paymentNo } AND member_no = #{ memberNo }")
    PaymentDTO findPaymentByPaymentNo(@Param("paymentNo") int paymentNo, @Param("memberNo") int memberNo);

    @Update("UPDATE payment SET confirm = 'Y', confirm_at = now() WHERE payment_no = #{ paymentNo }")
    int checkConfirm(int paymentNo);

    PaymentDTO selectPaymentDetail(int paymentNo);

    int findRefundStatus(String imPortId);
}