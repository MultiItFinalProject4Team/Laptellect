package com.multi.laptellect.product.service;

import com.multi.laptellect.product.model.dto.ProductCart;

import java.util.List;

/**
 * Please explain the class!!
 *
 * @author : 이강석
 * @fileName : CartService
 * @since : 2024-08-06
 */
public interface CartService {
    int processCart(int productNo) throws Exception;

    ProductCart getCartList() throws Exception;

    void updateCartProduct(String productNo, String quantity) throws Exception;

    void deleteCartProduct(List<String> productNos) throws Exception;
}
