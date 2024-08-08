package com.multi.laptellect.payment.controller;

import com.multi.laptellect.member.model.dto.MemberDTO;
import com.multi.laptellect.member.model.mapper.MemberMapper;
import com.multi.laptellect.member.service.MemberService;
import com.multi.laptellect.payment.model.dto.*;
import com.multi.laptellect.payment.service.PaymentService;
import com.multi.laptellect.product.model.dto.ProductCart;
import com.multi.laptellect.product.model.dto.ProductDTO;
import com.multi.laptellect.product.service.CartService;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentService paymentService;
    private final MemberMapper memberMapper;
    private final MemberService memberService;
    private final CartService cartService;

    public PaymentController(PaymentService paymentService, MemberMapper memberMapper, MemberService memberService, CartService cartService) {
        this.paymentService = paymentService;
        this.memberMapper = memberMapper;
        this.memberService = memberService;
        this.cartService = cartService;
    }

    @GetMapping("/orderlist")
    public String orderList(Model model) {
        int memberNo = SecurityUtil.getUserNo();
        MemberDTO memberDTO = memberMapper.findMemberByNo(memberNo);

        List<PaymentDTO> orderItems = paymentService.selectOrderItems(memberDTO.getMemberNo());
        List<String> reviewedOrders = paymentService.getReviewedOrders();

        model.addAttribute("orderItems", orderItems);
        model.addAttribute("reviewedOrders", reviewedOrders);
        return "/payment/orderlist";
    }

    @PostMapping("/payment")
    public String paymentpage(@RequestParam("imageUrl") String img,
                              @RequestParam("productName") String productName,
                              @RequestParam("price") int price,
                              Model model) {
        PaymentpageDTO paymentpageDTO = paymentService.findProduct(productName);
        paymentpageDTO.setImage(img);
        paymentpageDTO.setPrice(400);

        int memberNo = SecurityUtil.getUserNo();
        MemberDTO memberDTO = memberMapper.findMemberByNo(memberNo);
        PaymentpointDTO paymentpointDTO = paymentService.selectpoint(memberNo);

        model.addAttribute("paymentpageDTO", paymentpageDTO);
        model.addAttribute("paymentpointDTO", paymentpointDTO);
        model.addAttribute("memberDTO", memberDTO);

        return "/payment/payment";
    }

    @GetMapping("/cart-payment")
    public String cartPayment(Model model) {
        try {
            ProductCart productCart = cartService.getCartList();
            ArrayList<ProductDTO> cartList = productCart.getProducts();
            int totalQuantity = productCart.getTotalQuantity();

            int productTotal = cartList.size();
            int productTotalPrice = 0;

            for(int i = 0; i < cartList.size(); i++) {
                cartList.get(i).setPrice(300+i);
            }

            for(int i = 0; i < cartList.size(); i++) {
                productTotalPrice += cartList.get(i).getTotalPrice();
            }

            int memberNo = SecurityUtil.getUserNo();
            MemberDTO memberDTO = memberMapper.findMemberByNo(memberNo);
            System.out.println("ssd  " + cartList );

            model.addAttribute("cartList", cartList);
            model.addAttribute("total", productTotal);
            model.addAttribute("Quantity", totalQuantity);
            model.addAttribute("totalPrice", productTotalPrice);
            model.addAttribute("memberDTO", memberDTO);
        }
        catch (Exception e) {
            model.addAttribute("total", 0);
            model.addAttribute("Quantity", 0);
            model.addAttribute("totalPrice", 0);

        }
        return "payment/cart-payment";
    }

    @Transactional
    @PostMapping("/verifyPayment")
    public ResponseEntity<Map<String, Object>> verifyPayment(@RequestBody VerificationRequestDTO request) {
        try {
            int memberNo = SecurityUtil.getUserNo();
            MemberDTO memberDTO = memberMapper.findMemberByNo(memberNo);
            System.out.println(memberDTO);

            PaymentpageDTO paymentpageDTO = paymentService.findProduct(request.getProductName());

            PaymentDTO paymentDTO = new PaymentDTO();
            paymentDTO.setMemberNo(memberNo);
            paymentDTO.setProductNo(paymentpageDTO.getProductNo());
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

    @Transactional
    @PostMapping("/verifyCartPayment")
    public ResponseEntity<Map<String, Object>> verifyCartPayment(@RequestBody CartPaymentDTO request) {
        try {
            int memberNo = SecurityUtil.getUserNo();
            MemberDTO memberDTO = memberMapper.findMemberByNo(memberNo);

            // 포인트 사용 처리
            int usedPoints = Integer.parseInt(request.getUsedPoints());
            if (usedPoints > 0) {
                int newPoint = memberDTO.getPoint() - usedPoints;
                if (newPoint >= 0) {
                    memberDTO.setPoint(newPoint);
                    memberService.updatePoint(memberDTO);
                } else {
                    return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Insufficient points"));
                }
            }

            // 결제 검증
            boolean verified = paymentService.verifyCartPayment(request);

            if (verified) {
                // 포인트 사용 기록
                if (usedPoints > 0) {
                    PaymentpointDTO paymentpointDTO = new PaymentpointDTO();
                    paymentpointDTO.setMemberNo(memberNo);
                    paymentpointDTO.setImPortId(request.getImPortId());
                    paymentpointDTO.setUsedPoints(String.valueOf(usedPoints));
                    paymentService.usepoint(paymentpointDTO);
                }

                // 장바구니 비우기
                cartService.deleteCartProduct(request.getProducts().stream().map(p -> String.valueOf(p.getProductNo())).toList());

                return ResponseEntity.ok(Map.of("success", true, "message", "Cart payment verified successfully"));
            } else {
                throw new IllegalStateException("Payment amount mismatch");
            }
        } catch (Exception e) {
            // 롤백 처리
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Cart payment verification failed: " + e.getMessage()));
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
    @Transactional
    @PostMapping("/reviews")
    public ResponseEntity<Map<String, Object>> createReview(@RequestBody PaymentReviewDTO reviewDTO) {
        int memberNo = SecurityUtil.getUserNo();
        MemberDTO memberDTO = memberMapper.findMemberByNo(memberNo);

        String username = memberDTO.getMemberName();
        reviewDTO.setUserName(username);
        reviewDTO.setMemberNo(memberDTO.getMemberNo());

        // Fetch the product information using the imPortId
        PaymentDTO paymentDTO = paymentService.findPaymentByImPortId(reviewDTO.getImPortId());
        if (paymentDTO != null) {
            reviewDTO.setProductNo(paymentDTO.getProductNo());
        } else {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "주문 정보를 찾을 수 없습니다."));
        }

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