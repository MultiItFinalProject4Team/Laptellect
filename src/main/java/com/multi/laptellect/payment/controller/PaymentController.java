package com.multi.laptellect.payment.controller;

import com.multi.laptellect.member.model.dto.MemberDTO;
import com.multi.laptellect.member.model.mapper.MemberMapper;
import com.multi.laptellect.member.service.MemberService;
import com.multi.laptellect.payment.model.dto.*;
import com.multi.laptellect.payment.service.PaymentService;
import com.multi.laptellect.util.SecurityUtil;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentService paymentService;
    private final MemberMapper memberMapper;
    private final MemberService memberService;

    public PaymentController(PaymentService paymentService, MemberMapper memberMapper, MemberService memberService) {
        this.paymentService = paymentService;
        this.memberMapper = memberMapper;
        this.memberService = memberService;
    }

    /**
     * 주문내역페이지 구성
     *
     * @param model 뷰단으로 데이터이동
     * @return 주문내역페이지 열기
     */
    @GetMapping("/orderlist")
    public String orderList(Model model) {
        int memberNo = SecurityUtil.getUserNo();
        MemberDTO memberDTO = memberMapper.findMemberByNo(memberNo);

        List<OrderlistDTO> orderList = paymentService.selectOrders(memberDTO.getMemberName());
        List<String> reviewedOrders = paymentService.getReviewedOrders();
        model.addAttribute("orderList", orderList);
        model.addAttribute("reviewedOrders", reviewedOrders);
        return "/payment/orderlist";
    }

    /**
     * 주문 및 결제 페이지
     *
     * @param model 뷰단으로 데이터 이동
     * @return 결제페이지 열기
     */
//    @GetMapping("/payment")
//    public String selectpaymentpage(Model model) {
//        int memberNo = SecurityUtil.getUserNo();
//        MemberDTO memberDTO = memberMapper.findMemberByNo(memberNo);
//
//        PaymentpageDTO paymentpageDTO = paymentService.selectpaymentpage();
//        PaymentpointDTO paymentpointDTO = paymentService.selectpoint(memberNo);
//
//        model.addAttribute("paymentpageDTO", paymentpageDTO);
//        model.addAttribute("paymentpointDTO", paymentpointDTO);
//        model.addAttribute("memberDTO", memberDTO);
//
//        return "/payment/payment";
//    }

    @PostMapping("/payment")
    public String paymentpage(@RequestParam("img") String img,
                              @RequestParam("productName") String productName,
                              @RequestParam("price") int price,
                              Model model) {
        PaymentpageDTO paymentpageDTO = paymentService.findProduct(productName);
        paymentpageDTO.setImage(img);
        paymentpageDTO.setPrice(400);


        // 나머지 필요한 정보 설정
        int memberNo = SecurityUtil.getUserNo();
        MemberDTO memberDTO = memberMapper.findMemberByNo(memberNo);
        PaymentpointDTO paymentpointDTO = paymentService.selectpoint(memberNo);

//        model.addAttribute("paymentProductDTOList", paymentProductDTOList);
        model.addAttribute("paymentpageDTO", paymentpageDTO);
        model.addAttribute("paymentpointDTO", paymentpointDTO);
        model.addAttribute("memberDTO", memberDTO);


        return "/payment/payment";
    }

    /**
     * 결제검증 메서드
     * 결제시 사용포인트 조회 및 결제내역 저장
     * @param request 결제검증 dto
     * @return 결제 검증 결과 전송
     */
    @Transactional
    @PostMapping("/verifyPayment")
    public ResponseEntity<Map<String, Object>> verifyPayment(@RequestBody VerificationRequestDTO request) {
        try {
            int memberNo = SecurityUtil.getUserNo();
            MemberDTO memberDTO = memberMapper.findMemberByNo(memberNo);
            System.out.println(memberDTO);

            PaymentpageDTO paymentpageDTO = paymentService.findProduct(request.getProductName());

            PaymentDTO paymentDTO = new PaymentDTO();
            paymentDTO.setUsername(memberDTO.getMemberName());
//            paymentDTO.setProductinfo(paymentpageDTO.getProductInfo());
            paymentDTO.setProductName(paymentpageDTO.getProductName());
            paymentDTO.setProductPrice(paymentpageDTO.getPrice());
            paymentDTO.setPurchasePrice(request.getAmount().intValue());
            paymentDTO.setImPortId(request.getImPortId());

            PaymentpointDTO paymentpointDTO = paymentService.selectpoint(memberNo);
            paymentpointDTO.setUsedPoints(request.getUsedPoints());
            paymentpointDTO.setImPortId(request.getImPortId());

            // 포인트 사용 처리
            if (Integer.parseInt(paymentpointDTO.getUsedPoints()) > 0) {
                int newPoint = memberDTO.getPoint() - Integer.parseInt(paymentpointDTO.getUsedPoints());
                if (newPoint >= 0) {
                    memberDTO.setPoint(newPoint);
                    memberService.updatePoint(memberDTO);
                } else {
                    return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Insufficient points"));
                }
            }

            // 결제 검증
            boolean verified = paymentService.verifyPayment(request, paymentDTO);

            if (verified) {
                // 포인트 사용 기록
                if (Integer.parseInt(paymentpointDTO.getUsedPoints()) > 0) {
                    paymentService.usepoint(paymentpointDTO);
                }
                return ResponseEntity.ok(Map.of("success", true, "message", "Payment verified successfully"));
            } else {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Payment amount mismatch"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Payment verification failed: " + e.getMessage()));
        }
    }

    /**
     * 결제 취소 매서드
     * 결제취소시 사용된 포인트 반환 및 실결제금액 반환
     * @param cancelRequest 결제검증dto를 통한 결제취소 요청 데이터
     * @return 결제취소에 대한 성공여부 반환
     */
    @PostMapping("/cancel")
    public ResponseEntity<Map<String, Object>> cancelPayment(@RequestBody VerificationRequestDTO cancelRequest) {
        try {
            if (cancelRequest.getImPortId() == null || cancelRequest.getImPortId().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "취소 실패: 유효한 imPortId 값이 없습니다."));
            }

            IamportResponse<Payment> response = paymentService.cancelPayment(
                    cancelRequest.getImPortId(),
                    cancelRequest.getAmount(),
                    "고객 요청으로 인한 취소"
            );

            paymentService.updateRefundStatus(cancelRequest.getImPortId());
            int refundedPoints = paymentService.refundpoint(cancelRequest.getImPortId());

            int memberNo = SecurityUtil.getUserNo();
            MemberDTO memberDTO = memberMapper.findMemberByNo(memberNo);

            boolean reviewExists = paymentService.findReview(cancelRequest.getImPortId());

            if(reviewExists){
                try {
                    int newPoint = memberDTO.getPoint() + refundedPoints - 500;
                    memberDTO.setPoint(newPoint);
                    memberService.updatePoint(memberDTO);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }else{
                try {
                    int newPoint = memberDTO.getPoint() + refundedPoints;
                    memberDTO.setPoint(newPoint);
                    memberService.updatePoint(memberDTO);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }



            return ResponseEntity.ok(Map.of("success", true, "message", "결제가 성공적으로 취소되었습니다.", "data", response));
        } catch (IamportResponseException | IOException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "취소 실패: " + e.getMessage()));
        }
    }

    /**
     * 리뷰등록 매서드
     * 리뷰 등록 시 결제에 사용가능한 포인트 부여
     * @param reviewDTO 리뷰dto
     * @return 리뷰등록 성공여부 반환
     */
    @PostMapping("/reviews")
    public ResponseEntity<Map<String, Object>> createReview(@RequestBody PaymentReviewDTO reviewDTO) {

        int memberNo = SecurityUtil.getUserNo();
        MemberDTO memberDTO = memberMapper.findMemberByNo(memberNo);

        String username = memberDTO.getMemberName();
        reviewDTO.setUsername(username);
        int result = paymentService.saveReview(reviewDTO);


        Map<String, Object> response = new HashMap<>();
        if (result > 0) {
            PaymentpointDTO paymentpointDTO = paymentService.selectpoint(memberNo);
            paymentpointDTO.setImPortId(reviewDTO.getImPortId());
            paymentService.givepoint(paymentpointDTO);

            try {
                int newPoint = memberDTO.getPoint() + 500;
                memberDTO.setPoint(newPoint);
                memberService.updatePoint(memberDTO);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            response.put("success", true);
            response.put("message", "리뷰가 성공적으로 저장되었습니다. \n리뷰감사 포인트 500p를 지급합니다.\n" + "보유포인트 : " + memberDTO.getPoint() + "p");
        } else {
            response.put("success", false);
            response.put("message", "리뷰 저장에 실패했습니다.");
        }
        return ResponseEntity.ok(response);
    }
}