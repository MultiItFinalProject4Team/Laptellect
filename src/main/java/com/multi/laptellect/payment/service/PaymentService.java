package com.multi.laptellect.payment.service;

import com.multi.laptellect.api.payment.ApiKeys;
import com.multi.laptellect.member.model.dto.MemberDTO;
import com.multi.laptellect.member.model.mapper.MemberMapper;
import com.multi.laptellect.payment.model.dao.PaymentDAO;
import com.multi.laptellect.payment.model.dto.*;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import static java.lang.Math.abs;

@Service
public class PaymentService {

    private final ApiKeys apiKeys;
    private final MemberMapper memberMapper;



    @Autowired
    private PaymentDAO paymentDAO;

    @Autowired
    public PaymentService(ApiKeys apiKeys, MemberMapper memberMapper) {
        this.apiKeys = apiKeys;
        this.memberMapper = memberMapper;

    }

    public int usepoint(PaymentpointDTO paymentpointDTO) {
        return paymentDAO.usepoint(paymentpointDTO);
    }

    public int givepoint(PaymentpointDTO paymentpointDTO) { return paymentDAO.givepoint(paymentpointDTO); }

    public int refundReviewdPoint(PaymentpointDTO paymentpointDTO) { return paymentDAO.refundReviewdPoint(paymentpointDTO);
    }

    public int newMemberPoint(PaymentpointDTO paymentpointDTO) {
        return paymentDAO.newMemberPoint(paymentpointDTO);
    }

    @Transactional
    public PaymentpageDTO selectpaymentpage() {
        return paymentDAO.selectpaymentpage();
    }

    @Transactional
    public int insertPayment(PaymentDTO paymentDTO) {
        return paymentDAO.insertPayment(paymentDTO);
    }

    @Transactional
    public List<OrderlistDTO> selectOrders(String memberName) {
        return paymentDAO.selectOrders(memberName);
    }


    @Transactional
    public int updateRefundStatus(String imPortId) {
        return paymentDAO.updateRefundStatus(imPortId);
    }

    @Transactional
    public int findReviewedPoint(String imPortId) {
        return paymentDAO.findReviewedPoint(imPortId);
    }

    @Transactional
    public int refundpoint(String imPortId) {
        PaymentpointDTO paymentpointDTO = paymentDAO.select_refundpoint(imPortId);
        if (paymentpointDTO != null && paymentpointDTO.getPaymentPointChange() != null) {


            boolean reviewExists = findReview(imPortId);
            int usedPoints = abs(Integer.parseInt(paymentpointDTO.getPaymentPointChange()));

            if(reviewExists){
                paymentDAO.refundReviewdPoint(paymentpointDTO);
                paymentpointDTO.setUsedPoints(String.valueOf(Math.abs(paymentDAO.findReviewedPoint(imPortId))));
                if(Integer.parseInt(paymentpointDTO.getUsedPoints())>0) {
                    paymentDAO.refundpoint(paymentpointDTO);
                    usedPoints = Integer.parseInt(paymentpointDTO.getUsedPoints());
                    return usedPoints;
                }
            }else{
                if(usedPoints>0) {
                    paymentpointDTO.setUsedPoints(String.valueOf(usedPoints));
                    paymentDAO.refundpoint(paymentpointDTO);
                    return usedPoints;
                }
            }
        }
        return 0;
    }

    @Transactional
    public boolean verifyPayment(VerificationRequestDTO request, PaymentDTO paymentDTO) throws IamportResponseException, IOException {
        IamportClient client = new IamportClient(apiKeys.getIamportApiKey(), apiKeys.getIamportApiSecret());
        IamportResponse<Payment> payment = client.paymentByImpUid(request.getImPortId());

        BigDecimal actualAmount = payment.getResponse().getAmount();
        BigDecimal requestedAmount = request.getAmount();

        System.out.println("Actual Amount: " + actualAmount);
        System.out.println("Requested Amount: " + requestedAmount);
        System.out.println("Comparison result: " + actualAmount.compareTo(requestedAmount));

        if (actualAmount.compareTo(requestedAmount) == 0) {
            insertPayment(paymentDTO);
            return true;
        }
        return false;
    }

    @Transactional
    public IamportResponse<Payment> cancelPayment(String imPortId, BigDecimal requestAmount, String reason)
            throws IamportResponseException, IOException {
        IamportClient client = new IamportClient(apiKeys.getIamportApiKey(), apiKeys.getIamportApiSecret());

        // 먼저 결제 정보를 조회하여 취소 가능 금액 확인
        IamportResponse<Payment> paymentResponse = client.paymentByImpUid(imPortId);
        BigDecimal paidAmount = paymentResponse.getResponse().getAmount();
        BigDecimal canceledAmount = paymentResponse.getResponse().getCancelAmount();
        BigDecimal cancelableAmount = paidAmount.subtract(canceledAmount);

        if (cancelableAmount.compareTo(requestAmount) < 0) {
            throw new IllegalArgumentException("취소 요청 금액이 취소 가능 금액보다 큽니다.");
        }

        CancelData cancelData = new CancelData(imPortId, true);
        cancelData.setChecksum(requestAmount);
        cancelData.setReason(reason);


        return client.cancelPaymentByImpUid(cancelData);
    }

    @Transactional
    public int saveReview(PaymentReviewDTO paymentReviewDTO) {
        return paymentDAO.saveReview(paymentReviewDTO);
    }

    public List<String> getReviewedOrders() {
        return paymentDAO.getReviewedOrders();
    }


    public PaymentpointDTO selectpoint(int memberNo) {


        MemberDTO memberDTO = memberMapper.findMemberByNo(memberNo);

        int memberNO = memberDTO.getMemberNo();
        PaymentpointDTO paymentpointDTO = paymentDAO.selectpoint(memberNO);

        if (paymentpointDTO == null) {
            paymentpointDTO = new PaymentpointDTO();
            paymentpointDTO.setMemberNo(memberDTO.getMemberNo());
//            newMemberPoint(paymentpointDTO);

        }

        return paymentpointDTO;
    }

    public boolean findReview(String imPortId) {
        return paymentDAO.findReview(imPortId);
    }

}