package com.multi.laptellect.product.controller;


import com.multi.laptellect.product.model.dto.LaptopSpecDTO;
import com.multi.laptellect.product.model.dto.ProductDTO;
import com.multi.laptellect.product.model.dto.ProductInfo;
import com.multi.laptellect.product.service.CrawlingService;
import com.multi.laptellect.product.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/product")
public class ProductController {


    @Autowired
    CrawlingService crawlingService;

    @Autowired
    ProductService productService;


    //상품별 크롤링 검색 기능
    @PostMapping("/crawl")
    public String crawl(@RequestParam("products")String productType , Model model) throws IOException {
        int typeNo;
        String type;

        switch (productType) {
            case "1": // 노트북
                typeNo = 1;
                type = "laptop";
                break;
            case "2": // 마우스
                typeNo = 2;
                type = "mouse";
                break;
            case "3": // 키보드
                typeNo = 3;
                type = "keyboard";
                break;
            default:
                typeNo = 0; // 기본값 또는 에러 처리
                type = "unknown";
                break;
        }


     try {
         List<ProductInfo> products = crawlingService.crawlProducts(5 ,type);
         productService.saveProductsToDB(products,typeNo);

         model.addAttribute("products", products);

     } catch (Exception e){

         log.error("에러발생", e);

     }

        return "product/productList";
    }


    //상품리스트 조회
    @GetMapping("/productList")
    public String ProductList(Model model){
        List<ProductDTO> products = productService.getStoredProducts();
        products.get(0).getImage();

        System.out.println("이미지:" + products.get(0).getImage());

        model.addAttribute("products", products);


        return "product/productList";
    }


    //상세 정보
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

    @GetMapping("/keyboardDetails")
    public String keyboardDetails(@RequestParam("pcode") String pcode, Model model){

        return "product/keyboardDetails";
    }

    @PostMapping("/productType")
    public String productType(@RequestBody Map<String, Integer> request, Model model){
        int typeNo = request.get("typeNo");

        List<ProductDTO> products = productService.getTypeByProduct(typeNo);
        model.addAttribute("products", products);

        return "productList";
    }

}



