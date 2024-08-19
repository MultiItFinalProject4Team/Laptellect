package com.multi.laptellect.admin.product.controller;

import com.multi.laptellect.admin.product.service.AdminProductService;
import com.multi.laptellect.common.model.PagebleDTO;
import com.multi.laptellect.product.model.dto.ProductDTO;
import com.multi.laptellect.util.PaginationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Please explain the class!!
 *
 * @author : 이우석
 * @fileName : AdminProductController
 * @since : 2024-08-19
 */
@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminProductController {
    private final AdminProductService adminProductService;


    @GetMapping("/product/product_manage")
    public String showProductManagement(Model model) {

        return "admin/product/product_manage";
    }

    @PostMapping("/product/list")
    public String getProductList(@RequestBody PagebleDTO pagebleDTO, Model model) {

        try {

            Page<ProductDTO> products = adminProductService.getProductList(pagebleDTO);

            log.info("어드민페이지 = {}", products);
            int page = products.getPageable().getPageNumber();
            int size = products.getPageable().getPageSize();

            int startPage = PaginationUtil.getStartPage(products, 5);
            int endPage = PaginationUtil.getEndPage(products, 5);

            model.addAttribute("products", products);
            model.addAttribute("page", page);
            model.addAttribute("size", size);
            model.addAttribute("startPage", startPage);
            model.addAttribute("endPage", endPage);

        } catch (Exception e) {

            log.error("상품 내역 전체 조회 실패: {}", e.getMessage());
            log.error("Exception", e);
        }


        return "admin/product/product_list";
    }
}
