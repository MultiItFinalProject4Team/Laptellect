package com.multi.laptellect.payment.controller;

import com.multi.laptellect.member.model.dto.AddressDTO;
import com.multi.laptellect.member.model.dto.CustomUserDetails;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * The type Payment controller.
 */
@Controller
@RequestMapping("/payment")
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;
    private final MemberMapper memberMapper;
    private final MemberService memberService;
    private final CartService cartService;

    /**
     * PaymentService,MemberMapper,MemberService,CartService 생성자
     *
     * @param paymentService the payment service
     * @param memberMapper   the member mapper
     * @param memberService  the member service
     * @param cartService    the cart service
     */
    public PaymentController(PaymentService paymentService, MemberMapper memberMapper, MemberService memberService, CartService cartService) {
        this.paymentService = paymentService;
        this.memberMapper = memberMapper;
        this.memberService = memberService;
        this.cartService = cartService;
    }

    /**
     * 결제완료페이지
     *
     * @param impUid the imp uid
     * @param model  the model
     * @return the string
     * @throws Exception the exception
     */
    @GetMapping("/complete")
    public String paymentComplete(@RequestParam("impUid") String impUid, Model model) throws Exception {
        PaymentCompleteDTO paymentInfo = paymentService.getPaymentInfo(impUid);
        model.addAttribute("paymentInfo", paymentInfo);
        return "payment/payment-complete";
    }

//    @GetMapping("/orderlist")
//    public String orderList(Model model) {
//        int memberNo = SecurityUtil.getUserNo();
//        MemberDTO memberDTO = memberMapper.findMemberByNo(memberNo);
//
//        List<PaymentDTO> orderItems = paymentService.selectOrderItems(memberDTO.getMemberNo(), productNo);
//        List<String> reviewedOrders = paymentService.getReviewedOrders();
//
//        model.addAttribute("orderItems", orderItems);
//        model.addAttribute("reviewedOrders", reviewedOrders);
//        return "/payment/orderlist";
//    }

    /**
     * 결제페이지
     *
     * @param img         the img
     * @param productName the product name
     * @param price       the price
     * @param model       the model
     * @return the string
     * @throws Exception the exception
     */
    @PostMapping("/payment")
    public String paymentpage(@RequestParam("imageUrl") String img,
                              @RequestParam("productName") String productName,
                              @RequestParam("price") int price,
                              Model model) throws Exception {
        log.info("파라미터 = {} {} {}", img, productName, price);
        PaymentpageDTO paymentpageDTO = paymentService.findProduct(productName);
        paymentpageDTO.setImage(img);
        paymentpageDTO.setPrice(400);

        int memberNo = SecurityUtil.getUserNo();
        MemberDTO memberDTO = memberMapper.findMemberByNo(memberNo);
        PaymentpointDTO paymentpointDTO = paymentService.selectpoint(memberNo);

        ArrayList<AddressDTO> userAddressList = memberService.findAllAddressByMemberNo();




        if(userAddressList.isEmpty()){
            model.addAttribute("warningMessage", "기본배송지 설정 및 전화번호 인증을 먼저해주세요. \n\n사유 : 기본배송지 미설정");
            return "member/delivery-profile";
        } else if (memberDTO.getTel() == null) {

            CustomUserDetails userInfo = SecurityUtil.getUserDetails();

            model.addAttribute("warningMessage", "기본배송지 설정 및 전화번호 인증을 먼저해주세요. \n\n사유 : 전화번호 미인증");
            model.addAttribute("userInfo", userInfo);
            return "member/edit-profile";
        }


        AddressDTO userAddress = userAddressList.get(userAddressList.size()-1);

        model.addAttribute("addressList", userAddressList);
        model.addAttribute("userAddress", userAddress);
        model.addAttribute("paymentpageDTO", paymentpageDTO);
        model.addAttribute("paymentpointDTO", paymentpointDTO);
        model.addAttribute("memberDTO", memberDTO);

        return "payment/payment";
    }

    /**
     * 장바구니 결제페이지
     *
     * @param model the model
     * @return the string
     */
    @GetMapping("/cart-payment")
    public String cartPayment(Model model) {
        try {
            ProductCart productCart = cartService.getCartList();
            log.info("productCart 안에 값 = {}", productCart);
            ArrayList<ProductDTO> cartList = productCart.getProducts();
            log.info("장바구니 프로덕트 안에 값 = {}", cartList);
            int totalQuantity = productCart.getTotalQuantity();

            int productTotal = cartList.size();
            log.info("장바구니 사이즈 값 = {}", productTotal);


            int productTotalPrice = 0;

            for(int i = 0; i < cartList.size(); i++) {
                cartList.get(i).setPrice(300+i);
            }

            for(int i = 0; i < cartList.size(); i++) {
                productTotalPrice += cartList.get(i).getTotalPrice();
            }

            int memberNo = SecurityUtil.getUserNo();
            MemberDTO memberDTO = memberMapper.findMemberByNo(memberNo);


            ArrayList<AddressDTO> userAddressList = memberService.findAllAddressByMemberNo();

            if(userAddressList.isEmpty()){
                model.addAttribute("warningMessage", "기본배송지 설정 및 전화번호 인증을 먼저해주세요. \n\n사유 : 기본배송지 미설정");
                return "member/delivery-profile";
            } else if (memberDTO.getTel() == null) {

                CustomUserDetails userInfo = SecurityUtil.getUserDetails();

                model.addAttribute("warningMessage", "기본배송지 설정 및 전화번호 인증을 먼저해주세요. \n\n사유 : 전화번호 미인증");
                model.addAttribute("userInfo", userInfo);
                return "member/edit-profile";
            }

            AddressDTO userAddress = userAddressList.get(userAddressList.size()-1);
            System.out.println(userAddressList);
            System.out.println(userAddress);


            model.addAttribute("addressList", userAddressList);
            model.addAttribute("userAddress", userAddress);




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

    /**
     * 단일 결제검증
     *
     * @param request the request
     * @return the response entity
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
            paymentDTO.setMemberNo(memberNo);
            paymentDTO.setProductNo(paymentpageDTO.getProductNo());
            paymentDTO.setPurchasePrice(request.getAmount().intValue());
            paymentDTO.setImPortId(request.getImPortId());
            paymentDTO.setAddressId(request.getAddressId());
            paymentDTO.setQuantity(request.getQuantity());

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
                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "message", "Payment verified successfully",
                        "redirectUrl", "/payment/complete?impUid=" + request.getImPortId()
                ));
            } else {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Payment amount mismatch"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Payment verification failed: " + e.getMessage()));
        }
    }

    /**
     * 장바구니결제검증
     *
     * @param request the request
     * @return the response entity
     */
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

                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "message", "Cart payment verified successfully",
                        "redirectUrl", "/payment/complete?impUid=" + request.getImPortId()
                ));
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
     *
     * @param cancelRequest 결제검증dto를 통한 결제취소 요청 데이터
     * @return 결제취소에 대한 성공여부 반환
     */
    @Transactional
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

            paymentService.updateRefundStatus(cancelRequest.getImPortId(), cancelRequest.getPaymentNo());

            //장바구니로 주문했던 모든상품이 환불되었으면
            if(paymentService.findRefundStatus(cancelRequest.getImPortId()) == 0) {
                PaymentpointDTO paymentpointDTO = paymentService.select_refundpoint(cancelRequest.getImPortId());
                if (paymentpointDTO != null) {

                    int memberNo = SecurityUtil.getUserNo();
                    MemberDTO memberDTO = memberMapper.findMemberByNo(memberNo);


                    paymentpointDTO.setPaymentPointChange(Math.abs(paymentpointDTO.getPaymentPointChange()));


                    paymentService.refundPoint(paymentpointDTO);
                    int newPoint = memberDTO.getPoint() + paymentpointDTO.getPaymentPointChange();
                    memberDTO.setPoint(newPoint);
                    memberService.updatePoint(memberDTO);
                }
            }
            return ResponseEntity.ok(Map.of("success", true, "message", "결제가 성공적으로 취소되었습니다.", "data", response));
        } catch (IamportResponseException | IOException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "취소 실패: " + e.getMessage()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 리뷰등록 매서드
     * 리뷰 등록 시 결제에 사용가능한 포인트 부여
     *
     * @param reviewDTO 리뷰dto
     * @return 리뷰등록 성공여부 반환
     */
    @Transactional
    @PostMapping("/reviews")
    public ResponseEntity<Map<String, Object>> createReview(@RequestBody PaymentReviewDTO reviewDTO) {

        int result = paymentService.saveReview(reviewDTO);

        Map<String, Object> response = new HashMap<>();
        if (result > 0) {
            response.put("success", true);
            response.put("message", "리뷰가 성공적으로 저장되었습니다.");

            MemberDTO memberDTO = memberMapper.findMemberByNo(SecurityUtil.getUserNo());
            SecurityUtil.updateUserDetails(memberDTO);
        } else {
            response.put("success", false);
            response.put("message", "리뷰 저장에 실패했습니다.");
        }
        return ResponseEntity.ok(response);
    }

    /**
     * 상품상세페이지 리뷰수정 매서드
     *
     * @param reviewDTO the review dto
     * @return the response entity
     */
    @Transactional
    @PostMapping("/reviews/update")
    public ResponseEntity<Map<String, Object>> updateReview(@RequestBody PaymentReviewDTO reviewDTO) {
        try {
            int result = paymentService.updateReview(reviewDTO);
            Map<String, Object> response = new HashMap<>();
            if (result > 0) {
                response.put("success", true);
                response.put("message", "리뷰가 성공적으로 수정되었습니다.");
            } else {
                response.put("success", false);
                response.put("message", "리뷰 수정에 실패했습니다.");
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("리뷰 수정 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "리뷰 수정 중 오류가 발생했습니다."));
        }
    }

    /**
     * 상품상세페이지 리뷰삭제 매서드
     *
     * @param payload the payload
     * @return the response entity
     */
    @Transactional
    @PostMapping("/reviews/delete")
    public ResponseEntity<Map<String, Object>> deleteReview(@RequestBody Map<String, Integer> payload) {
        try {
            int paymentProductReviewsNo = payload.get("paymentProductReviewsNo");
            int result = paymentService.deleteReview(paymentProductReviewsNo);
            Map<String, Object> response = new HashMap<>();
            if (result > 0) {
                response.put("success", true);
                response.put("message", "리뷰가 성공적으로 삭제되었습니다.");
            } else {
                response.put("success", false);
                response.put("message", "리뷰 삭제에 실패했습니다.");
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("리뷰 삭제 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "리뷰 삭제 중 오류가 발생했습니다."));
        }
    }
}