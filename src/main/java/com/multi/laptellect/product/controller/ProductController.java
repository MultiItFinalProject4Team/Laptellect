package com.multi.laptellect.product.controller;


import com.multi.laptellect.product.model.dto.LaptopSpecDTO;
import com.multi.laptellect.product.model.dto.ProductInfo;
import com.multi.laptellect.product.model.dto.ProductTypeDTO;
import com.multi.laptellect.product.service.CrawlingService;
import com.multi.laptellect.product.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/product")
public class ProductController {


    @Autowired
    CrawlingService crawlingService;

    @Autowired
    ProductService productService;

    @PostMapping("/crawl")
    public String crawl(Model model) throws IOException {

        List<ProductInfo> products = crawlingService.crawlProducts(5);

        productService.saveProductsToDB(products);

        model.addAttribute("products", products);
        return "product/productList";
    }


    @GetMapping("/productList")
    public String ProductList(Model model){

        List<ProductInfo> products = productService.getStoredProducts();
        model.addAttribute("products", products);



        return "product/productList";
    }


    @GetMapping("/laptopDetails")
    public String productDetails(@RequestParam("pcode") String pcode, Model model) {
        log.info("1. 제품 세부정보 요청을 받았습니다.: {}", pcode);
        // 제품 상세 정보 가져오기
        ProductInfo productInfo = productService.getProductByCode(pcode);
        if (productInfo != null) {
            LaptopSpecDTO laptopSpecDTO = crawlingService.getLaptopDetails(productInfo);
            model.addAttribute("product", laptopSpecDTO);
            log.info("2. 제품에 대한 반품 보기: {}", productInfo.getProductName());
        } else {
            log.warn("3. 제품을 찾을 수 없습니다.: {}", pcode);
        }
        return "product/laptopDetails";
    }

    //상품 분류 가져오는곳
    @PostMapping("/productType")
    public String productType(Model model){

        List<ProductTypeDTO> types = productService.getAllProductTypes();
        model.addAttribute("types", types);

        return "productList";
    }

}



