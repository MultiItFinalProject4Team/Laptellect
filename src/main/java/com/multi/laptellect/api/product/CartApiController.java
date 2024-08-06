package com.multi.laptellect.api.product;

import com.multi.laptellect.product.model.dto.ProductCart;
import com.multi.laptellect.product.model.dto.ProductDTO;
import com.multi.laptellect.product.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

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
    public int processCart(@RequestParam(name = "productNo") int productNo) {
        try {
            return cartService.processCart(productNo);
        } catch (Exception e) {
            log.error("장바구니 Error = ", e);
            return 0;
        }
    }

    @GetMapping("/get-cartlist")
    public String getCartList(Model model) {
        try {
            ProductCart productCart = cartService.getCartList();
            ArrayList<ProductDTO> cartList = productCart.getProducts();
            int totalQuantity = productCart.getTotalQuantity();

            int productTotal = cartList.size();
            int productTotalPrice = 0;
            for(int i = 0; i < cartList.size(); i++) {
                productTotalPrice += cartList.get(i).getTotalPrice();
            }

            model.addAttribute("cartList", cartList);
            model.addAttribute("total", productTotal);
            model.addAttribute("Quantity", totalQuantity);
            model.addAttribute("totalPrice", productTotalPrice);
        } catch (Exception e) {
            log.error("장바구니 조회 실패 = ", e);
        }

        return "product/cart/cart-list";
    }

    @ResponseBody
    @PostMapping("update-cart-product")
    public boolean updateCartProduct(@RequestParam(name = "productNo") String productNo,
                                     @RequestParam(name = "quantity") String quantity) {
        try {
            cartService.updateCartProduct(productNo, quantity);
            log.info("수량 업데이트 성공 = {} {}", productNo, quantity);
            return true;
        } catch (Exception e) {
            log.error("상품 수량 업데이트 실패 = ", e);
            return false;
        }
    }

    @ResponseBody
    @PostMapping("delete-cart-product")
    public boolean updateCartProduct(@RequestParam(name = "productNo") List<String> productNos) {
        try {
            cartService.deleteCartProduct(productNos);
            log.info("장바구니 삭제 성공 = {}", productNos);
            return true;
        } catch (Exception e) {
            log.error("장바구니 삭제 실패 = ", e);
            return false;
        }
    }
}
