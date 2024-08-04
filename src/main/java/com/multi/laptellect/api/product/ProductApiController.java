package com.multi.laptellect.api.product;

import com.multi.laptellect.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 상품 API 처리 컨트롤러
 *
 * @author : 이강석
 * @fileName : ProductApiController
 * @since : 2024-08-05
 */
@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/api/product")
public class ProductApiController {
    private final ProductService productService;

    /**
     * 위시리스트 추가
     *
     * @param productNo 상품 번호
     * @return the boolean
     */
    @GetMapping("/add-wishlist")
    public boolean addToWishlist(@RequestParam(name = "productNo") int productNo) {
        try {
            boolean result = productService.addTowishlist(productNo);
        } catch (Exception e) {
            log.error("위시리스트 등록 실패");
        }
        return false;
    }
}
