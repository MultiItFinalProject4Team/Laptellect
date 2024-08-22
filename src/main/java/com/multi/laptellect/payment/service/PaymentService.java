package com.multi.laptellect.payment.service;

import com.multi.laptellect.api.payment.ApiKeys;
import com.multi.laptellect.common.model.PaginationDTO;
import com.multi.laptellect.member.model.dto.AddressDTO;
import com.multi.laptellect.member.model.dto.MemberDTO;
import com.multi.laptellect.member.model.mapper.MemberMapper;
import com.multi.laptellect.payment.model.dao.PaymentDAO;
import com.multi.laptellect.payment.model.dto.*;
import com.multi.laptellect.product.model.dto.ProductDTO;
import com.multi.laptellect.product.service.ProductService;
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

/**
 * The type Payment service.
 */
@Service
@Slf4j
public class PaymentService {

    private final ApiKeys apiKeys;
    private final MemberMapper memberMapper;
    private final ProductService productService;



    @Autowired
    private PaymentDAO paymentDAO;

    /**
     * ApiKeys,MemberMapper,ProductService 생성자
     *
     * @param apiKeys        the api keys
     * @param memberMapper   the member mapper
     * @param productService the product service
     */
    @Autowired
    public PaymentService(ApiKeys apiKeys, MemberMapper memberMapper, ProductService productService) {
        this.apiKeys = apiKeys;
        this.memberMapper = memberMapper;

        this.productService = productService;
    }

    /**
     * 포인트사용 매서드
     *
     * @param paymentpointDTO the paymentpoint dto
     * @return the int
     */
    public int usepoint(PaymentpointDTO paymentpointDTO) {
        return paymentDAO.usepoint(paymentpointDTO);
    }

    /**
     * Givepoint int.
     *
     * @param paymentpointDTO the paymentpoint dto
     * @return the int
     */
    public int givepoint(PaymentpointDTO paymentpointDTO) { return paymentDAO.givepoint(paymentpointDTO); }

    /**
     * Refund reviewd point int.
     *
     * @param paymentpointDTO the paymentpoint dto
     * @return the int
     */
    public int refundReviewdPoint(PaymentpointDTO paymentpointDTO) { return paymentDAO.refundReviewdPoint(paymentpointDTO);
    }

    /**
     * New member point int.
     *
     * @param paymentpointDTO the paymentpoint dto
     * @return the int
     */
    public int newMemberPoint(PaymentpointDTO paymentpointDTO) {
        return paymentDAO.newMemberPoint(paymentpointDTO);
    }

    /**
     * Selectpaymentpage paymentpage dto.
     *
     * @return the paymentpage dto
     */
    @Transactional
    public PaymentpageDTO selectpaymentpage() {
        return paymentDAO.selectpaymentpage();
    }

    /**
     * 결제내역 저장
     *
     * @param paymentDTO the payment dto
     * @return the int
     */
    @Transactional
    public int insertPayment(PaymentDTO paymentDTO) {
        return paymentDAO.insertPayment(paymentDTO);
    }

    /**
     * Select orders list.
     *
     * @param memberName the member name
     * @return the list
     */
    @Transactional
    public List<PaymentDTO> selectOrders(String memberName) {
        return paymentDAO.selectOrders(memberName);
    }


    /**
     * 환불상태 업데이트
     *
     * @param imPortId  the im port id
     * @param paymentNo the payment no
     * @return the int
     */
    @Transactional
    public int updateRefundStatus(String imPortId, Long paymentNo) {
        return paymentDAO.updateRefundStatus(imPortId, paymentNo);
    }

    /**
     * Find reviewed point int.
     *
     * @param imPortId the im port id
     * @return the int
     */
    @Transactional
    public int findReviewedPoint(String imPortId) {
        return paymentDAO.findReviewedPoint(imPortId);
    }

    /**
     * 주문내역에서 주문한 상품찾기
     * -> 리뷰및환불시 찾을때 필요
     * @param memberNo  the member no
     * @param productNo the product no
     * @return the payment dto
     */
    @Transactional
    public PaymentDTO selectOrderItems(int memberNo, int productNo) {
        return paymentDAO.selectOrderItems(memberNo, productNo);
    }

    /**
     * Find payments by im port id list.
     *
     * @param imPortId the im port id
     * @return the list
     */
    public List<PaymentDTO> findPaymentsByImPortId(String imPortId) {
        return paymentDAO.findPaymentsByImPortId(imPortId);
    }

    /**
     * 환불 시 포인트환불확인용 매서드
     *
     * @param imPortId the im port id
     * @return the paymentpoint dto
     */
    public PaymentpointDTO select_refundpoint(String imPortId){
        return paymentDAO.select_refundpoint(imPortId);
    }

    /**
     * 포인트환불
     *
     * @param paymentpointDTO the paymentpoint dto
     * @return the int
     */
    public int refundPoint(PaymentpointDTO paymentpointDTO){
        return paymentDAO.refundPoint(paymentpointDTO);
    }


    /**
     * 단일결제 유효성 검증
     *
     * @param request    the request
     * @param paymentDTO the payment dto
     * @return the boolean
     * @throws IamportResponseException the iamport response exception
     * @throws IOException              the io exception
     */
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

    /**
     * 장바구니결제 유효성검증
     *
     * @param request the request
     * @return the boolean
     * @throws IamportResponseException the iamport response exception
     * @throws IOException              the io exception
     */
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
                paymentDTO.setQuantity(product.getQuantity());

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

    /**
     * 결제취소매서드
     *
     * @param imPortId      the im port id
     * @param requestAmount the request amount
     * @param reason        the reason
     * @return the iamport response
     * @throws IamportResponseException the iamport response exception
     * @throws IOException              the io exception
     */
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

    /**
     * Save review int.
     *
     * @param paymentReviewDTO the payment review dto
     * @return the int
     */
    @Transactional
    public int saveReview(PaymentReviewDTO paymentReviewDTO) {
        return paymentDAO.saveReview(paymentReviewDTO);
    }

    /**
     * Gets reviewed orders.
     *
     * @return the reviewed orders
     */
    public List<String> getReviewedOrders() {
        return paymentDAO.getReviewedOrders();
    }


    /**
     * Find product paymentpage dto.
     *
     * @param productName the product name
     * @return the paymentpage dto
     */
    public PaymentpageDTO findProduct(String productName) {
        return paymentDAO.findProduct(productName);
    }

    /**
     * Selectpoint paymentpoint dto.
     *
     * @param memberNo the member no
     * @return the paymentpoint dto
     */
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

    /**
     * Find review boolean.
     *
     * @param imPortId the im port id
     * @return the boolean
     */
    public boolean findReview(String imPortId) {
        return paymentDAO.findReview(imPortId);
    }

    /**
     * Find payment by im port id payment dto.
     *
     * @param imPortId the im port id
     * @return the payment dto
     */
    public PaymentDTO findPaymentByImPortId(String imPortId) {
        return paymentDAO.findPaymentByImPortId(imPortId);
    }

    /**
     * Gets order list.
     *
     * @param pageable      the pageable
     * @param paginationDTO the pagination dto
     * @return the order list
     */
    @Transactional(readOnly = true)
    public Page<PaymentDTO> getOrderList(Pageable pageable, PaginationDTO paginationDTO) {
        int memberNo = SecurityUtil.getUserNo();

        ArrayList<PaymentDTO> orders = paymentDAO.findAllPaymentByMemberNo(pageable, paginationDTO, memberNo);
        log.info("주문 목록 조회 성공 = {}", orders);

        int total = paymentDAO.countPaymentByMemberNo(paginationDTO, memberNo);
        log.info("주문 목록 Total 조회 성공 = {}", orders);

        return new PageImpl<>(orders, pageable, total);
    }

    /**
     * Check confirm int.
     *
     * @param paymentNo the payment no
     * @return the int
     */
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

    /**
     * Select payment detail payment dto.
     *
     * @param paymentNo the payment no
     * @return the payment dto
     */
    public PaymentDTO selectPaymentDetail(int paymentNo) {
        return paymentDAO.selectPaymentDetail(paymentNo);
    }

    private AddressDTO selectPaymentAddress(int paymentNo) {
        return paymentDAO.selectPaymentAddress(paymentNo);
    }


    /**
     * Find refund status int.
     *
     * @param imPortId the im port id
     * @return the int
     */
    public int findRefundStatus(String imPortId) {
        return paymentDAO.findRefundStatus(imPortId);
    }


    /**
     * Payment detail payment detail dto.
     *
     * @param paymentNo the payment no
     * @return the payment detail dto
     */
    public PaymentDetailDTO paymentDetail(int paymentNo){
        PaymentDTO paymentDTO = selectPaymentDetail(paymentNo);
        AddressDTO addressDTO = selectPaymentAddress(paymentNo);

        int usedPoint = 0;

        if(findUsedPoint(paymentDTO.getImPortId()) != null) {
            usedPoint = Math.abs(findUsedPoint(paymentDTO.getImPortId()).getPaymentPointChange());
        }
        System.out.println(usedPoint);


        PaymentDetailDTO detailDTO = new PaymentDetailDTO();
        detailDTO.setPaymentDTO(paymentDTO);
        detailDTO.setAddressDTO(addressDTO);
        detailDTO.setPointChange(usedPoint);

        return detailDTO;
    }

    private PaymentpointDTO findUsedPoint(String imPortId) {
        return paymentDAO.findUsedPoint(imPortId);
    }


    /**
     * Find payment reviews by product no list.
     *
     * @param productNo the product no
     * @return the list
     */
    public List<PaymentReviewDTO> findPaymentReviewsByProductNo(int productNo) {
        return paymentDAO.findPaymentReviewsByProductNo(productNo);
    }

    /**
     * Gets payment info.
     *
     * @param impUid the imp uid
     * @return the payment info
     * @throws Exception the exception
     */
    public PaymentCompleteDTO getPaymentInfo(String impUid) throws Exception {
        List<PaymentDTO> payments = paymentDAO.findPaymentsByImPortId(impUid);
        PaymentpointDTO paymentpointDTO = paymentDAO.findUsedPoint(impUid);

        PaymentCompleteDTO paymentInfo = new PaymentCompleteDTO();
        List<ProductInfo> productInfoList = new ArrayList<>();
        int totalQuantity = 0;
        int totalPrice = 0;
        int count = 0;

        for (PaymentDTO payment : payments) {
            ProductDTO product = productService.findProductByProductNo(String.valueOf(payment.getProductNo()));

            ProductInfo productInfo = new ProductInfo();
            productInfo.setProductName(product.getProductName());
            productInfo.setQuantity(payment.getQuantity());
            productInfo.setPrice(product.getPrice());
            productInfo.setTotalPrice(payment.getPurchasePrice());
            productInfo.setImage(product.getImage());

            productInfoList.add(productInfo);

            totalQuantity += payment.getQuantity();
            totalPrice += payment.getPurchasePrice();
            count++;
        }

        paymentInfo.setProducts(productInfoList);
        paymentInfo.setTotalQuantity(totalQuantity);


        int usedPoints = 0;
        if (paymentpointDTO != null) {
            usedPoints = Math.abs(Integer.parseInt(String.valueOf(paymentpointDTO.getPaymentPointChange())));
        }

        paymentInfo.setDiscountAmount(usedPoints);
        if(count > 1) {
            paymentInfo.setTotalPrice(totalPrice - usedPoints);
        }else{
            paymentInfo.setTotalPrice(totalPrice);
        }

        return paymentInfo;
    }

    /**
     * Update review int.
     *
     * @param reviewDTO the review dto
     * @return the int
     */
    @Transactional
    public int updateReview(PaymentReviewDTO reviewDTO) {
        return paymentDAO.updateReview(reviewDTO);
    }

    /**
     * Delete review int.
     *
     * @param paymentProductReviewsNo the payment product reviews no
     * @return the int
     */
    @Transactional
    public int deleteReview(int paymentProductReviewsNo) {
        return paymentDAO.deleteReview(paymentProductReviewsNo);
    }
}