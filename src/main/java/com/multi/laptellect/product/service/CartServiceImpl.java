package com.multi.laptellect.product.service;

import com.multi.laptellect.util.RedisUtil;
import com.multi.laptellect.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Cart(장바구니) Service
 *
 * @author : 이강석
 * @fileName : CartServiceImpl
 * @since : 2024-08-06
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService{
    private final RedisUtil redisUtil;

    @Override
    public int processCart(int productNo) throws Exception {
        String memberName = SecurityUtil.getUserDetails().getMemberName();
        memberName = "cart:" + memberName;
        Long duration = 24L * 60L * 60L;
        String productNoString = String.valueOf(productNo);

        // 회원 장바구니 존재 시 Insert, 존재 안할 시 생성 후 insert
        log.debug("회원 장바구니 존재 여부 검증 시작 = {}", memberName);
        if(redisUtil.getAllHashData(memberName) != null) {
            // 상품이 Redis에 존재하면 Delete, 존재하지 않을 시 insert
            log.debug("선택 상품 검증 시작 = {}", productNoString);
            if(redisUtil.getHashData(memberName, productNoString) != null) {
                // 상품 delete
                redisUtil.deleteHashData(memberName, productNoString);
                log.info("상품 Delete = {}", productNoString);
                return 2;
            } else {
                //상품 insert
                redisUtil.setHashDataExpire(memberName, productNoString, String.valueOf(1), duration);
                log.info("상품 insert = {} {} {} {}", memberName, productNoString, "1개", "24시간");
                log.info("장바구니 상품 확인 = {}", redisUtil.getAllHashData(memberName)); // 확인 후 삭제
                return 1;
            }
        } else {
            // 상품이 Redis에 존재하면 Delete, 존재하지 않을 시 insert
            log.debug("선택 상품 검증 시작 = {}", productNoString);
            if(redisUtil.getHashData(memberName, productNoString) != null) {
                // 상품 delete
                redisUtil.deleteHashData(memberName, productNoString);
                log.info("상품 Delete = {}", productNoString);
                return 2;
            } else {
                //상품 insert
                redisUtil.setHashDataExpire(memberName, productNoString, String.valueOf(1), duration);
                log.info("상품 insert = {} {} {} {}", memberName, productNoString, "1개", "24시간");
                return 1;
            }
        }
    }
}
