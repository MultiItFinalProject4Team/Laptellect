package com.multi.laptellect.payment.controller;

import com.multi.laptellect.payment.model.dto.*;
import com.multi.laptellect.payment.service.PaymentService;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Controller
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentService paymentService;



    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/orderlist")
    public String orderList(Model model) {
        List<OrderlistDTO> orderList = paymentService.selectOrders();
        List<String> reviewedOrders = paymentService.getReviewedOrders();
        model.addAttribute("orderList", orderList);
        model.addAttribute("reviewedOrders", reviewedOrders);
        return "/payment/orderlist";
    }

    @GetMapping("/payment")
    public String selectpaymentpage(Model model) { //로그인된 유저의 이름으로 바꾸기
        PaymentpageDTO paymentpageDTO = paymentService.selectpaymentpage();
        PaymentpointDTO paymentpointDTO = paymentService.selectpoint();
        model.addAttribute("paymentpageDTO", paymentpageDTO);
        model.addAttribute("paymentpointDTO", paymentpointDTO);

        return "/payment/payment";
    }

    @Transactional
    @PostMapping("/verifyPayment")
    public ResponseEntity<Map<String, Object>> verifyPayment(@RequestBody VerificationRequestDTO request) {
        try {
            PaymentpageDTO paymentpageDTO = paymentService.selectpaymentpage();
            PaymentDTO paymentDTO = new PaymentDTO();
            paymentDTO.setUsername2(paymentpageDTO.getUsername1());
            paymentDTO.setProductinfo2(paymentpageDTO.getProductinfo1());
            paymentDTO.setProductname2(paymentpageDTO.getProductname1());
            paymentDTO.setProductprice2(request.getAmount().intValue());
            paymentDTO.setImd2(request.getImpUid());

            boolean verified = paymentService.verifyPayment(request, paymentDTO);
            System.out.println(request.getUsedPoints());

            PaymentpointDTO paymentpointDTO = paymentService.selectpoint();
            paymentpointDTO.setUsedPoints(request.getUsedPoints());

            if(paymentpointDTO.getPossessionpoint() <= 0) {
                verified = false;
            }
            else{
                if (Integer.parseInt(paymentpointDTO.getUsedPoints()) > 0)
                    paymentService.usepoint(paymentpointDTO);
            }



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

            paymentService.updateRefundStatus(cancelRequest.getImpUid());

            return ResponseEntity.ok(Map.of("success", true, "message", "결제가 성공적으로 취소되었습니다.", "data", response));
        } catch (IamportResponseException | IOException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "취소 실패: " + e.getMessage()));
        }
    }

    @PostMapping("/reviews")
    public ResponseEntity<Map<String, Object>> createReview(@RequestBody PaymentReviewDTO reviewDTO) {
        int result = paymentService.saveReview(reviewDTO);
        Map<String, Object> response = new HashMap<>();
        if (result > 0) {
            PaymentpointDTO paymentpointDTO = paymentService.selectpoint();
            paymentService.givepoint(paymentpointDTO);

            response.put("success", true);
            response.put("message", "리뷰가 성공적으로 저장되었습니다. \n리뷰감사 포인트 500p를 지급합니다.\n" + "보유포인트 : " + paymentpointDTO.getPossessionpoint() + "p");
        } else {
            response.put("success", false);
            response.put("message", "리뷰 저장에 실패했습니다.");
        }
        return ResponseEntity.ok(response);
    }
}