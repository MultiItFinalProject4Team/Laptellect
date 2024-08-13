package com.multi.laptellect.payment.service;

import com.multi.laptellect.api.payment.ApiKeys;
import com.multi.laptellect.common.model.PaginationDTO;
import com.multi.laptellect.member.model.dto.AddressDTO;
import com.multi.laptellect.member.model.dto.MemberDTO;
import com.multi.laptellect.member.model.mapper.MemberMapper;
import com.multi.laptellect.payment.model.dao.PaymentDAO;
import com.multi.laptellect.payment.model.dto.*;
import com.multi.laptellect.product.model.dto.ProductDTO;
import com.multi.laptellect.util.SecurityUtil;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class PaymentService {

    private final ApiKeys apiKeys;
    private final MemberMapper memberMapper;



    @Autowired
    private PaymentDAO paymentDAO;

    @Autowired
    public PaymentService(ApiKeys apiKeys, MemberMapper memberMapper) {
        this.apiKeys = apiKeys;
        this.memberMapper = memberMapper;

    }

    public int usepoint(PaymentpointDTO paymentpointDTO) {
        return paymentDAO.usepoint(paymentpointDTO);
    }

    public int givepoint(PaymentpointDTO paymentpointDTO) { return paymentDAO.givepoint(paymentpointDTO); }

    public int refundReviewdPoint(PaymentpointDTO paymentpointDTO) { return paymentDAO.refundReviewdPoint(paymentpointDTO);
    }

    public int newMemberPoint(PaymentpointDTO paymentpointDTO) {
        return paymentDAO.newMemberPoint(paymentpointDTO);
    }

    @Transactional
    public PaymentpageDTO selectpaymentpage() {
        return paymentDAO.selectpaymentpage();
    }

    @Transactional
    public int insertPayment(PaymentDTO paymentDTO) {
        return paymentDAO.insertPayment(paymentDTO);
    }

    @Transactional
    public List<PaymentDTO> selectOrders(String memberName) {
        return paymentDAO.selectOrders(memberName);
    }


    @Transactional
    public int updateRefundStatus(String imPortId, Long paymentNo) {
        return paymentDAO.updateRefundStatus(imPortId, paymentNo);
    }

    @Transactional
    public int findReviewedPoint(String imPortId) {
        return paymentDAO.findReviewedPoint(imPortId);
    }

    @Transactional
    public List<PaymentDTO> selectOrderItems(int memberNo) {
        return paymentDAO.selectOrderItems(memberNo);
    }

    public List<PaymentDTO> findPaymentsByImPortId(String imPortId) {
        return paymentDAO.findPaymentsByImPortId(imPortId);
    }

    public PaymentpointDTO select_refundpoint(String imPortId){
        return paymentDAO.select_refundpoint(imPortId);
    }

    public int refundPoint(PaymentpointDTO paymentpointDTO){
        return paymentDAO.refundPoint(paymentpointDTO);
    }



    @Transactional
    public boolean verifyPayment(VerificationRequestDTO request, PaymentDTO paymentDTO) throws IamportResponseException, IOException {
        IamportClient client = new IamportClient(apiKeys.getIamportApiKey(), apiKeys.getIamportApiSecret());
        IamportResponse<Payment> payment = client.paymentByImpUid(request.getImPortId());

        BigDecimal actualAmount = payment.getResponse().getAmount();
        BigDecimal requestedAmount = request.getAmount();

        System.out.println("Actual Amount: " + actualAmount);
        System.out.println("Requested Amount: " + requestedAmount);
        System.out.println("Comparison result: " + actualAmount.compareTo(requestedAmount));

        if (actualAmount.compareTo(requestedAmount) == 0) {
            insertPayment(paymentDTO);
            return true;
        }
        return false;
    }

    @Transactional
    public boolean verifyCartPayment(CartPaymentDTO request) throws IamportResponseException, IOException {
        IamportClient client = new IamportClient(apiKeys.getIamportApiKey(), apiKeys.getIamportApiSecret());
        IamportResponse<Payment> payment = client.paymentByImpUid(request.getImPortId());

        BigDecimal actualAmount = payment.getResponse().getAmount();
        BigDecimal requestedAmount = request.getTotalAmount();

        if (actualAmount.compareTo(requestedAmount) == 0) {
            // 각 상품에 대해 결제 정보 저장
            for (ProductDTO product : request.getProducts()) {
                PaymentDTO paymentDTO = new PaymentDTO();
                paymentDTO.setMemberNo(SecurityUtil.getUserNo());
                paymentDTO.setProductNo(product.getProductNo());
                paymentDTO.setPurchasePrice(product.getTotalPrice());
                paymentDTO.setImPortId(request.getImPortId());

                try {
                    if (product.getProductNo() == 0) {
                        throw new IllegalArgumentException("Invalid product number: " + product.getProductName());
                    }
                    paymentDTO.setAddressId(request.getAddressId());
                    insertPayment(paymentDTO);
                } catch (Exception e) {
                    // 로그 기록 및 예외 처리
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    throw new RuntimeException("Failed to insert payment for product: " + product.getProductName(), e);
                }
            }
            return true;
        }
        return false;
    }

    @Transactional
    public IamportResponse<Payment> cancelPayment(String imPortId, BigDecimal requestAmount, String reason)
            throws IamportResponseException, IOException {
        IamportClient client = new IamportClient(apiKeys.getIamportApiKey(), apiKeys.getIamportApiSecret());

        // 먼저 결제 정보를 조회하여 취소 가능 금액 확인
        IamportResponse<Payment> paymentResponse = client.paymentByImpUid(imPortId);
        BigDecimal paidAmount = paymentResponse.getResponse().getAmount();
        BigDecimal canceledAmount = paymentResponse.getResponse().getCancelAmount();
        BigDecimal cancelableAmount = paidAmount.subtract(canceledAmount);

        if (cancelableAmount.compareTo(requestAmount) < 0) {
            log.info("Cancelable amount: {}, Request amount: {}", cancelableAmount, requestAmount);
            requestAmount = cancelableAmount;
            log.info("Cancelable amount: {}, Request amount: {}", cancelableAmount, requestAmount);
        }

        CancelData cancelData = new CancelData(imPortId, true, requestAmount);
        cancelData.setReason(reason);

        return client.cancelPaymentByImpUid(cancelData);
    }

    @Transactional
    public int saveReview(PaymentReviewDTO paymentReviewDTO) {
        return paymentDAO.saveReview(paymentReviewDTO);
    }

    public List<String> getReviewedOrders() {
        return paymentDAO.getReviewedOrders();
    }




    public PaymentpageDTO findProduct(String productName) {
        return paymentDAO.findProduct(productName);
    }

    public PaymentpointDTO selectpoint(int memberNo) {

        MemberDTO memberDTO = memberMapper.findMemberByNo(memberNo);

        int memberNO = memberDTO.getMemberNo();
        PaymentpointDTO paymentpointDTO = paymentDAO.selectpoint(memberNO);

        if (paymentpointDTO == null) {
            paymentpointDTO = new PaymentpointDTO();
            paymentpointDTO.setMemberNo(memberDTO.getMemberNo());
//            newMemberPoint(paymentpointDTO);

        }

        return paymentpointDTO;
    }

    public boolean findReview(String imPortId) {
        return paymentDAO.findReview(imPortId);
    }

    public PaymentDTO findPaymentByImPortId(String imPortId) {
        return paymentDAO.findPaymentByImPortId(imPortId);
    }

    @Transactional(readOnly = true)
    public Page<PaymentDTO> getOrderList(Pageable pageable, PaginationDTO paginationDTO) {
        int memberNo = SecurityUtil.getUserNo();

        ArrayList<PaymentDTO> orders = paymentDAO.findAllPaymentByMemberNo(pageable, paginationDTO, memberNo);
        log.info("주문 목록 조회 성공 = {}", orders);

        int total = paymentDAO.countPaymentByMemberNo(paginationDTO, memberNo);
        log.info("주문 목록 Total 조회 성공 = {}", orders);

        return new PageImpl<>(orders, pageable, total);
    }

    @Transactional
    public int checkConfirm(int paymentNo) {
        int memberNo = SecurityUtil.getUserNo();

        // 사용자와 상품을 구매한 사용자가 동일한지 검증
        PaymentDTO paymentInfo = paymentDAO.findPaymentByPaymentNo(paymentNo, memberNo);
        if(paymentInfo.getMemberNo() != memberNo) return 2;
        if(paymentInfo.getConfirm().equals("Y")) return 3;

        // 구매 확정 confirm 컬럼 업데이트
        int result = paymentDAO.checkConfirm(paymentNo);
        log.info("구매 확정 완료 = {}", result);

        return result;
    }

    public PaymentDTO selectPaymentDetail(int paymentNo) {
        return paymentDAO.selectPaymentDetail(paymentNo);
    }

    private AddressDTO selectPaymentAddress(int paymentNo) {
        return paymentDAO.selectPaymentAddress(paymentNo);
    }


    public int findRefundStatus(String imPortId) {
        return paymentDAO.findRefundStatus(imPortId);
    }



    public PaymentDetailDTO paymentDetail(int paymentNo){
        PaymentDTO paymentDTO = selectPaymentDetail(paymentNo);
        AddressDTO addressDTO = selectPaymentAddress(paymentNo);

        PaymentDetailDTO detailDTO = new PaymentDetailDTO();
        detailDTO.setPaymentDTO(paymentDTO);
        detailDTO.setAddressDTO(addressDTO);

        return detailDTO;
    }


}