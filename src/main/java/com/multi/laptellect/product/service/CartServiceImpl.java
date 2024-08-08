package com.multi.laptellect.product.service;

import com.multi.laptellect.product.model.dto.ProductCart;
import com.multi.laptellect.product.model.dto.ProductDTO;
import com.multi.laptellect.util.RedisUtil;
import com.multi.laptellect.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    private final ProductService productService;

    @Override
    public int processCart(int productNo) throws Exception {
        // 로그인 하지 않은 사용자는 사용이 불가능 하므로 로그인 여부를 의미하는 3 반환
        if(!SecurityUtil.isAuthenticated()) return 3;

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

    @Override
    public ProductCart getCartList() throws Exception{
        String memberName = SecurityUtil.getUserDetails().getMemberName();
        memberName = "cart:" + memberName;
        int sum = 0;

        Map<String, String> cartList = redisUtil.getAllHashData(memberName);
        log.info("카트 리스트 반환 = {}", cartList);

        if(cartList.isEmpty()) return null;

        ArrayList<ProductDTO> products = new ArrayList<>();

        Set<String> productNos = cartList.keySet();
        for(String productNo : productNos) {
            ProductDTO product = productService.findProductByProductNo(productNo);
            int quantity = Integer.parseInt(cartList.get(productNo));
            product.setQuantity(quantity);
            products.add(product);
            sum += quantity;
        }
        log.info("ProductList = {}", products);

        ProductCart productCart = new ProductCart();
        productCart.setProducts(products);
        productCart.setTotalQuantity(sum);

        return productCart;
    }

    @Override
    public void updateCartProduct(String productNo, String quantity) {
        String memberName = SecurityUtil.getUserDetails().getMemberName();
        memberName = "cart:" + memberName;
        Long duration = 24L * 60L * 60L;

        log.debug("장바구니 상품 수량 업데이트 시작 = {}", memberName);
        redisUtil.updateHashDataExpire(memberName, productNo, quantity, duration);
        log.info("장바구니 상품 수량 업데이트 성공 = {}", memberName);
    }

    @Override
    public void deleteCartProduct(List<String> productNos) {
        String memberName = SecurityUtil.getUserDetails().getMemberName();
        memberName = "cart:" + memberName;

        log.debug("장바구니 상품 삭제 시작 = {}", productNos);
        for(String productNo : productNos) {
            redisUtil.deleteHashData(memberName, productNo);
        }
        log.debug("장바구니 상품 삭제 성공 = {}", productNos);
    }
}
