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
    /**
     * The Crawling service.
     */
    @Autowired
    CrawlingService crawlingService;

    /**
     * The Product service.
     */
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
    public String crawlProducts(@RequestParam("products") String productType, Model model) throws IOException {
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
                    crawlingService.processLaptopDetails();
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


    /**
     * Product list string.
     *
     * @param model the model
     * @return the string
     */
    @GetMapping("/productList")
    public String ProductList(Model model,
                              @RequestParam(name = "pageNumber", defaultValue = "1") int pageNumber,
                              @RequestParam(name = "pageSize",defaultValue = "12") int pageSize) {

        //페이징 처리
        List<ProductDTO> products = productService.getStoredProducts(pageNumber, pageSize);

        // 전체 제품 수
        int totalProducts = productService.getTotalProducts();

        // 총 페이지 계산
        int totalPages = (int) Math.ceil((double) totalProducts / pageSize);

        int displayPageCount = 10;
        int startPage = Math.max(1, pageNumber - (displayPageCount / 2));
        int endPage = Math.min(totalPages, startPage + displayPageCount - 1);

        model.addAttribute("products", products);
        model.addAttribute("pageNumber", pageNumber);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "product/productList";
    }


    /**
     * Product details string.
     *
     * @param productCode the product code
     * @param model       the model
     * @return the string
     */
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


            model.addAttribute("options", options);
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

    @GetMapping("/review")
    public void review() {
        ProductDTO productDTO = new ProductDTO();

        crawlingService.reviewCrawler(productDTO);
    }

}



