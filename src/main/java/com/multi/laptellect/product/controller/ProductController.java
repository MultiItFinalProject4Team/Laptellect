package com.multi.laptellect.product.controller;


import com.multi.laptellect.product.model.dto.ProductDTO;
import com.multi.laptellect.product.model.dto.ProductInfo;
import com.multi.laptellect.product.service.LaptopDetailsService;
import com.multi.laptellect.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/product")
public class ProductController {


    @Autowired
    ProductService productService;

    @Autowired
    LaptopDetailsService laptopDetailsService;

    @GetMapping("/productList")
    public String laptopCrawl(@RequestParam(value = "pages", defaultValue = "1") int pages, Model model) throws IOException {
        List<ProductInfo> products = productService.crawlProducts(pages);
        model.addAttribute("products", products);
        return "product/productList";
    }

    @GetMapping("/laptopDetails")
    public String getProductDetails(@RequestParam("pcode") String pcode, Model model) throws IOException {

        ProductInfo productInfo = productService.crawlProducts(1).stream()
                .filter(product -> product.getPcode().equals(pcode))
                .findFirst()
                .orElse(null);

        if (productInfo == null) {
            // 상품 정보를 찾을 수 없는 경우 처리
            return "error/404";
        }



        ProductDTO productDetails = laptopDetailsService.getProductDetails(productInfo);
        model.addAttribute("productInfo", productInfo);
        model.addAttribute("productDetails", productDetails);

        return "product/laptopDetails";
    }


}
