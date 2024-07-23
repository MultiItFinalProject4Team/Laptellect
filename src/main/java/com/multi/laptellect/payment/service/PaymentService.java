package com.multi.laptellect.payment.service;

import com.multi.laptellect.api.payment.ApiKeys;
import com.multi.laptellect.payment.model.dto.InsertDTO;
import com.multi.laptellect.payment.model.dto.VerificationRequestDTO;
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

@Service
public class PaymentService {

    private final ApiKeys apiKeys;
    private final TestService testService;

    @Autowired
    public PaymentService(ApiKeys apiKeys, TestService testService) {
        this.apiKeys = apiKeys;
        this.testService = testService;
    }

    @Transactional
    public boolean verifyPayment(VerificationRequestDTO request, InsertDTO insertDTO) throws IamportResponseException, IOException {
        IamportClient client = new IamportClient(apiKeys.getIamportApiKey(), apiKeys.getIamportApiSecret());
        IamportResponse<Payment> payment = client.paymentByImpUid(request.getImpUid());

        if (payment.getResponse().getAmount().compareTo(request.getAmount()) == 0) {
            testService.insertTest(insertDTO);
            System.out.println(insertDTO);
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
}