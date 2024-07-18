package com.multi.laptellect.payment.controller;


import com.multi.laptellect.api.payment.ApiKeys;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.math.BigDecimal;

@Controller
@RequestMapping("/payment")
public class PaymentController {

    @GetMapping("/payment")
    public void paymentpage(){

    }

    private final ApiKeys apiKeys;

    // ApiKeys 객체를 생성자 주입으로 받습니다.
    @Autowired
    public PaymentController(ApiKeys apiKeys) {
        this.apiKeys = apiKeys;
    }

    @PostMapping("/verifyPayment")
    public ResponseEntity<String> verifyPayment(@RequestBody VerificationRequest request) {
        // ApiKeys 객체에서 API 키와 시크릿을 가져와 IamportClient를 초기화합니다.
        System.out.println("검증을 시작합니다");
        IamportClient client = new IamportClient(apiKeys.getIamportApiKey(), apiKeys.getIamportApiSecret());

        try {
            IamportResponse<Payment> payment = client.paymentByImpUid(request.getImpUid());
            //포트원 api를 호출하여 주어진 imp_uid에 해당하는 결제정보를 가져옴
            System.out.println(payment);


            if (payment.getResponse().getAmount().compareTo(BigDecimal.valueOf(request.getAmount())) == 0) {
                // api에서 받아온 결제금액과 요청을 받은 금액을 비교함
                System.out.println("검증성공");
                return ResponseEntity.ok("Payment verified successfully");
            } else {
                System.out.println("검증실패 결제값 다름");
                return ResponseEntity.badRequest().body("Payment amount mismatch");
            }
        } catch (IamportResponseException | IOException e) {
            System.out.println("이건그냥에러");
            return ResponseEntity.badRequest().body("Payment verification failed: " + e.getMessage());
        }
    }
}

class VerificationRequest {
    private String impUid;
    private long amount;

    // Getters and setters
    public String getImpUid() {
        return impUid;
    }

    public void setImpUid(String impUid) {
        this.impUid = impUid;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }
}
