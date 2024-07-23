package com.multi.laptellect.product.controller;


import com.multi.laptellect.product.model.dto.ProductDTO;
import com.multi.laptellect.product.service.CrawlingService;
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
    CrawlingService crawlingService;

    @Autowired
    LaptopDetailsService laptopDetailsService;

    @Autowired
    ProductService productService;

    @GetMapping("/productList")
    public String laptopCrawl() throws IOException {
        crawlingService.crawlAndSaveProducts(2); // 크롤링 페이지 수 설정
        return "redirect:/product/productList";
    }

    @GetMapping("/products")
    public String viewProducts(Model model) {
        List<ProductDTO> productList = productService.getAllProducts();
        model.addAttribute("products", productList);
        return "product/list";
    }

    @GetMapping("/productDetails")
    public String getProductDetails(@RequestParam("productCode") String productCode, Model model) {
        model.addAttribute("product", productService.getProductByCode(productCode));
        return "product/productDetails";
    }


}
