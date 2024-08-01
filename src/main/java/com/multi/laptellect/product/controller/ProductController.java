package com.multi.laptellect.product.controller;


import com.multi.laptellect.product.model.dto.LaptopDetailsDTO;
import com.multi.laptellect.product.model.dto.ProductDTO;
import com.multi.laptellect.product.service.CrawlingService;
import com.multi.laptellect.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The type Product controller.
 */
@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/product")
public class ProductController {


    @Autowired
    CrawlingService crawlingService;

    @Autowired
    ProductService productService;


    /**
     * Crawl string.
     *
     * @param productType the product type
     * @param model       the model
     * @return the string
     * @throws IOException the io exception
     */
//상품별 크롤링 검색 기능
    @PostMapping("/test")
    public String crawl(@RequestParam("products") String productType,
                        Model model) throws IOException {
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
            List<ProductDTO> products = crawlingService.crawlProducts(type);
            log.info("제품 확인 {}", products);

            productService.saveProductsToDB(products, typeNo);


            if (!products.isEmpty()) {
                for (int i = 0; i < products.size(); i++) {
                    log.info("products 내용확인 = {}", products);
                    log.info("조회 횟수 확인 = {}", i + 1);

                    ProductDTO productDTO = products.get(i); // 예시로 첫 번째 제품 사용

                    log.info("제품 코드 확인{}", productDTO.getProductCode());
                    log.info("제품 코드 확인{}", productDTO.getProductCode());

                    crawlingService.processAllLaptopDetails();

                    model.addAttribute("products", products);
                    //  model.addAttribute("specDTO", specDTO); // 필요에 따라 추가
                }

            }

        } catch (IOException e) {

            log.error("1.에러발생", e);

        } catch (Exception e) {
            log.error("2.에러발생", e);
        }
        return "product/productList";

    }


    /**
     * Product list string.
     *
     * @param model the model
     * @return the string
     */
//상품리스트 조회
    @GetMapping("/productList")
    public String ProductList(Model model) {
        List<ProductDTO> products = productService.getStoredProducts();

        model.addAttribute("products", products);

        return "product/productList";
    }

    /**
     * Product details string.
     *
     * @param productCode the product code
     * @param model       the model
     * @return the string
     */
//상세 정보
    @GetMapping("/laptopDetails")
    public String productDetails(@RequestParam("productCode") String productCode,
                                 Model model) {
        log.info("1. 제품 세부정보 요청을 받았습니다.: {}", productCode);
        // 제품 상세 정보 가져오기
        List<LaptopDetailsDTO> laptopDetails = productService.getLaptopProductDetails(productCode);
        log.info("상세정보를 조회 = {}: ", laptopDetails);
        if (!laptopDetails.isEmpty()) {
            LaptopDetailsDTO details = laptopDetails.get(0);

            model.addAttribute("productName", details.getProductName());
            model.addAttribute("price", details.getPrice());
            model.addAttribute("img", details.getUploadName());

            log.info("details 확인작업 = {}", details);

            List<String> options = new ArrayList<>();
            List<String> optionsValue = new ArrayList<>();

            for (LaptopDetailsDTO detail : laptopDetails) {
                options.add(detail.getOptions());
                optionsValue.add(detail.getOptionValue());
            }




            model.addAttribute("options",options);
            model.addAttribute("optionValue", optionsValue);


        }


        return "product/laptopDetails";

    }


    @PostMapping("/productType")
    public String productType(@RequestBody Map<String, Integer> request, Model model) {
        int typeNo = request.get("typeNo");

        List<ProductDTO> products = productService.getTypeByProduct(typeNo);
        model.addAttribute("products", products);

        return "productList";
    }


}



