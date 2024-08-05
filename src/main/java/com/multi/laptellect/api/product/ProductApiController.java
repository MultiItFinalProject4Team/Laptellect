package com.multi.laptellect.api.product;

import com.multi.laptellect.product.model.dto.WishlistDTO;
import com.multi.laptellect.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @ResponseBody
    @PostMapping("/process-wishlist")
    public int processWishlist(@RequestParam(name = "productNo") List<Integer> productNo) {
        int result = 500;
        try {
            result = productService.processWishlist(productNo);
        } catch (Exception e) {
            log.error("위시리스트 등록 실패");
        }
        return result;
    }

    @GetMapping("/get-wishlist")
    public String getWishlist(Model model, @PageableDefault(size = 5) Pageable pageable) {
        try {
            Page<WishlistDTO> wishlist = productService.getWishlist(pageable);

            int page = pageable.getPageNumber();
            int size = pageable.getPageSize();
            long total = wishlist.getTotalElements();

            model.addAttribute("wishlist", wishlist);
            model.addAttribute("total", total);
            model.addAttribute("page", page);
            model.addAttribute("size", size);
        } catch (Exception e) {
            log.error("위시리스트 조회 실패 = ", e);
        }
        return "member/wishlist/wishlist";
    }

    @ResponseBody
    @PostMapping("/process-basket")
    public int processBasket(@RequestParam(name = "productNo") int productNo) {
        try {
            return productService.processBasket(productNo);
        } catch (Exception e) {
            log.error("장바구니 Error = ", e);
            return 0;
        }
    }
}
