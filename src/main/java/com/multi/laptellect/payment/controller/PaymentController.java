package com.multi.laptellect.payment.controller;

import com.multi.laptellect.payment.model.dto.InsertDTO;
import com.multi.laptellect.payment.model.dto.OrderlistDTO;
import com.multi.laptellect.payment.model.dto.TestDTO;
import com.multi.laptellect.payment.model.dto.VerificationRequestDTO;
import com.multi.laptellect.payment.service.PaymentService;
import com.multi.laptellect.payment.service.TestService;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentService paymentService;
    private final TestService testService;
    private InsertDTO insertDTO;
    private TestDTO testDTO;
    private OrderlistDTO orderlistDTO;

    @Autowired
    public PaymentController(PaymentService paymentService, TestService testService) {
        this.paymentService = paymentService;
        this.testService = testService;
        this.insertDTO = new InsertDTO();
    }


    @GetMapping("/orderlist")
    public String orderList(Model model) {
        List<OrderlistDTO> orderList = testService.selectAllOrders();
        System.out.println("1" + orderList);
        model.addAttribute("orderList", orderList);
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
    public ResponseEntity<Map<String, Object>> verifyPayment(@RequestBody VerificationRequestDTO request) {
        try {
            insertDTO.setUsername2(testDTO.getUsername1());
            insertDTO.setProductDTO2(testDTO.getProductDTO1());
            insertDTO.setProductname2(testDTO.getProductname1());
            insertDTO.setProductprice2(testDTO.getProductprice1()); // 할인금액
            insertDTO.setImd2(request.getImpUid());

            boolean verified = paymentService.verifyPayment(request, insertDTO);

            if (verified) {
                return ResponseEntity.ok(Map.of("success", true, "message", "Payment verified successfully"));
            } else {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Payment amount mismatch"));
            }
        } catch (IamportResponseException | IOException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Payment verification failed: " + e.getMessage()));
        }
    }

    @PostMapping("/cancel")
    public ResponseEntity<Map<String, Object>> cancelPayment(@RequestBody VerificationRequestDTO cancelRequest) {
        try {
            if (cancelRequest.getImpUid() == null || cancelRequest.getImpUid().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "취소 실패: 유효한 impUid 값이 없습니다."));
            }

            IamportResponse<Payment> response = paymentService.cancelPayment(
                    cancelRequest.getImpUid(),
                    cancelRequest.getAmount(),
                    "고객 요청으로 인한 취소"
            );

            testService.updateRefundStatus(cancelRequest.getImpUid());

            return ResponseEntity.ok(Map.of("success", true, "message", "결제가 성공적으로 취소되었습니다.", "data", response));
        } catch (IamportResponseException | IOException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "취소 실패: " + e.getMessage()));
        }
    }
}