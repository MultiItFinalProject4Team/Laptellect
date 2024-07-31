package com.multi.laptellect.payment.service;

import com.multi.laptellect.api.payment.ApiKeys;
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

@Service
public class PaymentService {

    private final ApiKeys apiKeys;


    @Autowired
    private PaymentDAO paymentDAO;

    @Autowired
    public PaymentService(ApiKeys apiKeys) {
        this.apiKeys = apiKeys;
    }

    public int usepoint(PaymentpointDTO paymentpointDTO){
        return paymentDAO.usepoint(paymentpointDTO);
    }

    public int givepoint(PaymentpointDTO paymentpointDTO){
        return paymentDAO.givepoint(paymentpointDTO);
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
    public List<OrderlistDTO> selectAllOrders() {
        return paymentDAO.selectAllOrders();
    }

    @Transactional
    public int updateRefundStatus(String ImpUid) {
        return paymentDAO.updateRefundStatus(ImpUid);
    }

    @Transactional
    public boolean verifyPayment(VerificationRequestDTO request, PaymentDTO paymentDTO) throws IamportResponseException, IOException {
        IamportClient client = new IamportClient(apiKeys.getIamportApiKey(), apiKeys.getIamportApiSecret());
        IamportResponse<Payment> payment = client.paymentByImpUid(request.getImpUid());

        if (payment.getResponse().getAmount().compareTo(request.getAmount()) == 0) {
            insertPayment(paymentDTO);
            System.out.println(paymentDTO);
            return true;
        }
        return false;
    }

    @Transactional
    public IamportResponse<Payment> cancelPayment(String impUid, BigDecimal requestAmount, String reason)
            throws IamportResponseException, IOException {
        IamportClient client = new IamportClient(apiKeys.getIamportApiKey(), apiKeys.getIamportApiSecret());

        // 먼저 결제 정보를 조회하여 취소 가능 금액 확인
        IamportResponse<Payment> paymentResponse = client.paymentByImpUid(impUid);
        BigDecimal paidAmount = paymentResponse.getResponse().getAmount();
        BigDecimal canceledAmount = paymentResponse.getResponse().getCancelAmount();
        BigDecimal cancelableAmount = paidAmount.subtract(canceledAmount);

        if (cancelableAmount.compareTo(requestAmount) < 0) {
            throw new IllegalArgumentException("취소 요청 금액이 취소 가능 금액보다 큽니다.");
        }

        CancelData cancelData = new CancelData(impUid, true);
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

    public PaymentpointDTO selectpoint() {
        String username = "jack";  // 고정된 username 사용
        PaymentpointDTO paymentpointDTO = paymentDAO.selectpoint(username);

        if (paymentpointDTO == null) {
            paymentpointDTO = new PaymentpointDTO();
            paymentpointDTO.setUsername(username);
            paymentpointDTO.setPossessionpoint(0);  // 기본값 설정
        }

        return paymentpointDTO;
    }


}