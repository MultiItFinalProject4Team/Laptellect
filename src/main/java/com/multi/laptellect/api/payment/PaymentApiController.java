package com.multi.laptellect.api.payment;

import com.multi.laptellect.payment.model.dto.PaymentDTO;
import com.multi.laptellect.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * PaymentAPI 처리
 *
 * @author : 이강석
 * @fileName : PaymentApiController
 * @since : 2024-08-08
 */
@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("api/payment")
public class PaymentApiController {
    private final PaymentService paymentService;



    @ResponseBody
    @PostMapping("/confirm")
    public int checkConfirm(@RequestParam(name = "paymentNo") int paymentNo) {
        int result = 0;

        try {
            result = paymentService.checkConfirm(paymentNo);
        } catch (Exception e) {
            log.error("상품 구매 확정 Error = ", e);
        }
        return result;
    }


    @GetMapping("/detail")
    @ResponseBody
    public PaymentDTO getPaymentDetail(@RequestParam("paymentNo") int paymentNo) {
        try {
            return paymentService.getPaymentDetail(paymentNo);
        } catch (Exception e) {
            log.error("주문 상세 정보 조회 실패", e);
            return null;
        }
    }
}
