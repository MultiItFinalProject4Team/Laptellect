package com.multi.laptellect.payment.service;

import com.multi.laptellect.api.payment.ApiKeys;
import com.multi.laptellect.payment.model.dto.InsertDTO;
import com.multi.laptellect.payment.model.dto.VerificationRequestDto;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
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
    public boolean verifyPayment(VerificationRequestDto request, InsertDTO insertDTO) throws IamportResponseException, IOException {
        IamportClient client = new IamportClient(apiKeys.getIamportApiKey(), apiKeys.getIamportApiSecret());
        IamportResponse<Payment> payment = client.paymentByImpUid(request.getImpUid());

        if (payment.getResponse().getAmount().compareTo(BigDecimal.valueOf(request.getAmount())) == 0) {
            testService.insertTest(insertDTO);
            System.out.println(insertDTO);
            return true;
        }
        return false;
    }
}