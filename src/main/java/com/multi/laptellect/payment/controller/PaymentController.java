package com.multi.laptellect.payment.controller;

import com.multi.laptellect.payment.model.dto.InsertDTO;
import com.multi.laptellect.payment.model.dto.TestDTO;
import com.multi.laptellect.payment.model.dto.VerificationRequestDto;
import com.multi.laptellect.payment.service.PaymentService;
import com.multi.laptellect.payment.service.TestService;
import com.siot.IamportRestClient.exception.IamportResponseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Controller
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentService paymentService;
    private final TestService testService;
    private InsertDTO insertDTO;
    private TestDTO testDTO;

    @Autowired
    public PaymentController(PaymentService paymentService, TestService testService) {
        this.paymentService = paymentService;
        this.testService = testService;
        this.insertDTO = new InsertDTO();
    }

    @GetMapping("/orderlist")
    public String orderlist(Model model){
        testDTO = testService.selectTest();
        model.addAttribute("testDTO", testDTO);
        System.out.println(testDTO);
        return "/payment/orderlist";
    }

    @GetMapping("/payment")
    public String selectTest(Model model) {
        testDTO = testService.selectTest();
        model.addAttribute("testDTO", testDTO);
        System.out.println(testDTO);
        return "/payment/payment";
    }

    @PostMapping("/verifyPayment")
    public ResponseEntity<String> verifyPayment(@RequestBody VerificationRequestDto request) {
        try {
            insertDTO.setUsername2(testDTO.getUsername1());
            insertDTO.setProductname2(testDTO.getProductname1());
            insertDTO.setProductprice2(testDTO.getProductprice1() - 100); // 할인금액

            boolean verified = paymentService.verifyPayment(request, insertDTO);

            if (verified) {
                return ResponseEntity.ok("Payment verified successfully");
            } else {
                return ResponseEntity.badRequest().body("Payment amount mismatch");
            }
        } catch (IamportResponseException | IOException e) {
            return ResponseEntity.badRequest().body("Payment verification failed: " + e.getMessage());
        }
    }
}