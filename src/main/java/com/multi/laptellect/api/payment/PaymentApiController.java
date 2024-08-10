package com.multi.laptellect.api.payment;

import com.multi.laptellect.member.model.dto.MemberDTO;
import com.multi.laptellect.member.model.mapper.MemberMapper;
import com.multi.laptellect.payment.model.dto.PaymentDTO;
import com.multi.laptellect.payment.service.PaymentService;
import com.multi.laptellect.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
    private final MemberMapper memberMapper;



    @ResponseBody
    @PostMapping("/confirm")
    public int checkConfirm(@RequestParam(name = "paymentNo") int paymentNo, Model model) {
        int result = 0;

        try {
            result = paymentService.checkConfirm(paymentNo);

            // 포인트 변동 됐으므로 세션 업데이트
            int memberNo = SecurityUtil.getUserNo();

            MemberDTO updateUserInfo = memberMapper.findMemberByNo(memberNo);
            int userPoint = updateUserInfo.getPoint();

            SecurityUtil.updateUserDetails(updateUserInfo);
        } catch (Exception e) {
            log.error("상품 구매 확정 Error = ", e);
        }
        return result;
    }

    @GetMapping("/point")
    @ResponseBody
    public int getUserPoint() {
        int memberNo = SecurityUtil.getUserNo();
        MemberDTO updateUserInfo = memberMapper.findMemberByNo(memberNo);
        int userPoint = updateUserInfo.getPoint();

        return userPoint;
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
