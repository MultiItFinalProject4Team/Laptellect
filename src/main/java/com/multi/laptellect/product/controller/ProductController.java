package com.multi.laptellect.product.controller;


import com.multi.laptellect.product.model.dto.ProductDTO;
import com.multi.laptellect.product.model.dto.laptop.LaptopSpecDTO;
import com.multi.laptellect.product.service.CartService;
import com.multi.laptellect.product.service.CrawlingService;
import com.multi.laptellect.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.List;

/**
 * The type Product controller.
 */
@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/product")
public class ProductController {
    private final CartService cartService;
    private final CrawlingService crawlingService;
    private final ProductService productService;

    /**
     * 크롤링을 시작합니다.
     *
     * @param productType 상품의 타입(노트북,키보드,마우스)
     * @return the string
     */
//상품별 크롤링 검색 기능
    @PostMapping("/test")
    public String crawlProducts(@RequestParam(name = "products") String productType, Model model) throws IOException {
        int typeNo;

        switch (productType) {
            case "1": // 노트북
                typeNo = 1;

                break;
            case "2": // 마우스
                typeNo = 2;
                break;
            case "3": // 키보드
                typeNo = 3;
                break;
            default:
                typeNo = 0; // 기본값 또는 에러 처리
                break;
        }

        try {
            List<ProductDTO> products = crawlingService.crawlProducts(typeNo);


            log.info("제품 확인 {}", products);


            productService.saveProductsToDB(products, typeNo);

            int count = 0; // 상세 정보 크롤링 카운트

            log.info("상품 타입 확인 = {}", typeNo);
            switch (typeNo) {
                case 1:
                    crawlingService.processLaptopDetails(typeNo);
                    log.info("productController = {}", typeNo);
                    break;
                case 2:
                    // crawlingService.processMouseDetails(productDTO);
                    break;
                case 3:
                    //   crawlingService.processKeyboardDetails(productDTO);
                    break;
            }

            model.addAttribute("products", products);

        } catch (IOException e) {

            log.error("1.에러발생", e);

        } catch (Exception e) {
            log.error("2.에러발생", e);
        }
        return "product/productList";

    }

    @GetMapping("/laptopList")
    public String LaptopList() {
        int typeNo = 1;

        productService.getProductByType(typeNo);


        return "product/laptop/laptopList";

    }

    @GetMapping("/mouseList")
    public String mouseList() {
        int typeNo = 2;

        return "product/mouse/mouseList";

    }

    @GetMapping("/keyboardList")
    public String keyboardList() {
        int typeNo = 3;


        return "product/keyboard/keyboardList";

    }


    /**
     * Product list string.
     *
     * @param model the model
     * @return the string
     */
    @GetMapping("/productList")
    public String ProductList(Model model,
                              @RequestParam(name = "typeNo", defaultValue = "1") int typeNo) {
        model.addAttribute("typeNo", typeNo);
        return "product/productList";
    }




    /**
     * Product details string.
     *
     * @param productNo the product code
     * @param model     the model
     * @return the string
     */
    @GetMapping("/laptop/laptopDetails")
    public String productDetails(@RequestParam(name = "productNo") int productNo,
                                 Model model) {
        log.info("1. 제품 세부정보 요청을 받았습니다.: {}", productNo);

        // 제품 상세 정보 가져오기
        LaptopSpecDTO laptop = productService.getLaptopProductDetails(productNo);

        model.addAttribute("productNo",laptop.getProductNo());
        model.addAttribute("laptop", laptop);

        return "product/laptop/laptopDetails";
    }


    @GetMapping("/review")
    public void review() {
        ProductDTO productDTO = new ProductDTO();

        crawlingService.reviewCrawler();
    }

    @GetMapping("/cart")
    public String showCartList() {
        return "product/productCart";
    }

}



