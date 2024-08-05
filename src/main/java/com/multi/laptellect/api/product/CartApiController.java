package com.multi.laptellect.api.product;

import com.multi.laptellect.product.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Please explain the class!!
 *
 * @author : 이강석
 * @fileName : CartAjaxController
 * @since : 2024-08-06
 */
@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/api/product")
public class CartApiController {
    private final CartService cartService;

    @ResponseBody
    @PostMapping("/process-cart")
    public int processBasket(@RequestParam(name = "productNo") int productNo) {
        try {
            return cartService.processCart(productNo);
        } catch (Exception e) {
            log.error("장바구니 Error = ", e);
            return 0;
        }
    }
}
