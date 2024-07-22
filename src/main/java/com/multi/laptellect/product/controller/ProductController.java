package com.multi.laptellect.product.controller;


import com.multi.laptellect.product.model.dto.ProductInfo;
import com.multi.laptellect.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.List;

@Controller
public class ProductController {


    @Autowired
    ProductService productService;

    @GetMapping("/crawl")
    public String laptopCrawl(@RequestParam("pages") int pages, Model model) throws IOException {
        List<ProductInfo> products = productService.crawlProducts(pages);
        model.addAttribute("products", products);
        return "productList";
    }


}
