package com.multi.laptellect.product.controller;


import com.multi.laptellect.product.model.dto.LaptopDetailsDTO;
import com.multi.laptellect.product.model.dto.ProductDTO;
import com.multi.laptellect.product.service.CartService;
import com.multi.laptellect.product.model.dto.SpecDTO;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    public String LaptopList(){
        int typeNo = 1;

        productService.getProductByType(typeNo);


        return "product/laptopList";

    }

    @GetMapping("/mouseList")
    public String mouseList(){
        int typeNo = 2;

        return "product/mouseList";

    }

    @GetMapping("/keyboardList")
    public String keyboardList(){
        int typeNo = 3;


        return "product/keyboardList";

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
                              @RequestParam(name = "pageSize", defaultValue = "12") int pageSize) {

        //페이징 처리
        List<ProductDTO> products = productService.getStoredProducts(pageNumber, pageSize);

        log.info("productList 확인 = {}", products);


        Set<String> neededOptions = Set.of("운영체제(OS)", "제조사", "램 용량", "저장 용량", "해상도", "화면 크기", "GPU 종류", "코어 수", "CPU 넘버");

        for(ProductDTO productDTO : products){
            List<SpecDTO> filteredSpecs = productService.filterSpecs(productDTO.getProductNo(), neededOptions);
            productDTO.setSpecs(filteredSpecs);
            log.info("필터링된 Spec 값 전달 확인 ={}", filteredSpecs);

            String specsString = filteredSpecs.stream()
                    .map(spec -> spec.getOptions() + ": " + spec.getOptionValue())
                    .collect(Collectors.joining(" | "));
            productDTO.setSpecsString(specsString);


        }

        model.addAttribute("products", products);


        // 전체 제품 수
        int totalProducts = productService.getTotalProducts();

        // 총 페이지 계산
        int totalPages = (int) Math.ceil((double) totalProducts / pageSize);

        int displayPageCount = 10;
        int startPage = Math.max(1, pageNumber - (displayPageCount / 2));
        int endPage = Math.min(totalPages, startPage + displayPageCount - 1);

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
     * @param productNo the product code
     * @param model       the model
     * @return the string
     */
    @GetMapping("/laptopDetails")
    public String productDetails(@RequestParam(name = "productNo") int productNo,
                                 Model model) {
        log.info("1. 제품 세부정보 요청을 받았습니다.: {}", productNo);
        // 제품 상세 정보 가져오기
        List<LaptopDetailsDTO> laptopDetails = productService.getLaptopProductDetails(productNo);
        log.info("상세정보를 조회 = {}: ", laptopDetails);
        if (!laptopDetails.isEmpty()) {
            LaptopDetailsDTO details = laptopDetails.get(0);

            log.info("details.getProductNo ={}",details.getProductNo());

            model.addAttribute("productNo",details.getProductNo());
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



