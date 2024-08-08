package com.multi.laptellect.api.product;

import com.multi.laptellect.product.model.dto.ProductDTO;
import com.multi.laptellect.product.model.dto.SpecDTO;
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
import java.util.Set;
import java.util.stream.Collectors;

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

    @GetMapping("/product-list")
    public String getProductList(Model model,
                                 @RequestParam(name = "typeNo", defaultValue = "1") int typeNo) {
        List<ProductDTO> products = productService.getStoredProducts(typeNo);
        for (ProductDTO productDTO : products) {
            int productNo = productDTO.getProductNo();

            switch (typeNo) {
                case 1: // 노트북
                    log.info("laptop Get Spec = {}", typeNo);
                    Set<String> neededOptions = Set.of("운영체제(OS)", "제조사", "램 용량", "저장 용량", "해상도", "화면 크기", "GPU 종류", "코어 수", "CPU 넘버");
                    List<SpecDTO> filteredSpecs = productService.filterSpecs(productNo, neededOptions);
                    productDTO.setSpecs(filteredSpecs);
                    log.info("필터링된 Spec 값 전달 확인 ={}", filteredSpecs);

                    String specsString = filteredSpecs.stream()
                            .map(spec -> spec.getOptions() + ": " + spec.getOptionValue())
                            .collect(Collectors.joining(" | "));
                    productDTO.setSpecsString(specsString);
                    break;
                case 2: // 마우스
                    log.info("Mouse Get Spec = {}", typeNo);
                    break;
                case 3: // 키보드
                    log.info("keyboard Get Spec = {}", typeNo);
                    break;
            }
        }
        model.addAttribute("products", products);

        return "product/product/productList";
    }
}
