package com.multi.laptellect.product.controller;


import com.multi.laptellect.customer.dto.ProductqList;
import com.multi.laptellect.customer.service.CustomerService;
import com.multi.laptellect.customer.service.PaginationService;
import com.multi.laptellect.product.model.dto.KeyBoardSpecDTO;
import com.multi.laptellect.product.model.dto.ProductDTO;
import com.multi.laptellect.product.model.dto.SpecDTO;
import com.multi.laptellect.product.model.dto.laptop.LaptopSpecDTO;
import com.multi.laptellect.product.service.CartService;
import com.multi.laptellect.product.service.CrawlingService;
import com.multi.laptellect.product.service.ProductService;
import com.multi.laptellect.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
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
    private final CustomerService customerService;
    private final PaginationService paginationService;

    /**
     * 크롤링을 시작합니다.
     *
     * @param productType 상품의 타입(노트북,키보드,마우스)
     * @return the string
     */
//상품별 크롤링 검색 기능
    @GetMapping("/test")
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
                              @RequestParam(name = "typeNo", defaultValue = "1") int typeNo)
                               {
        model.addAttribute("typeNo", typeNo);
        return "product/productList";
    }

    @GetMapping("/search")
    public String searchProducts(@RequestParam(name = "typeNo") int typeNo,
                                 @RequestParam(name = "keyword" , required = false) String keyword,
                                 Model model) {
        List<ProductDTO> products  = productService.searchProducts(keyword,typeNo);
        for (ProductDTO productDTO : products) {
            int productNo = productDTO.getProductNo();

            String detailUrl;

            switch (typeNo) {
                case 1: // 노트북
                    log.info("laptop Get Spec = {}", typeNo);
                    Set<String> neededOptions = Set.of("운영체제(OS)", "제조사", "램 용량", "저장 용량", "해상도", "화면 크기", "GPU 종류", "코어 수", "CPU 넘버");
                    List<SpecDTO> filteredSpecs = productService.filterSpecs(productNo, neededOptions);
                    productDTO.setSpecs(filteredSpecs);
                    log.info("필터링된 Spec 값 전달 확인 ={}", filteredSpecs);

                    String specsString = filteredSpecs.stream()
                            .map(spec -> spec.getOptions() + ": " + spec.getOptionValue())
                            .collect(Collectors.joining(" | "));
                    productDTO.setSpecsString(specsString);
                    detailUrl = "/product/laptop/laptopDetails?productNo=" + productNo;
                    productDTO.setUrl(detailUrl);
                    break;
                case 2: // 마우스
                    log.info("Mouse Get Spec = {}", typeNo);
                    break;
                case 3: // 키보드
                    log.info("keyboard Get Spec = {}", typeNo);
                    Set<String> neededOptions1 = Set.of("제조사", "연결 방식", "사이즈", "인터페이스", "접점 방식", "스위치", "가로", "세로");
                    List<SpecDTO> filteredSpecs1 = productService.filterSpecs(productNo, neededOptions1);
                    productDTO.setSpecs(filteredSpecs1);
                    log.info("필터링된 Spec 값 전달 확인 ={}", filteredSpecs1);

                    String specsString1 = filteredSpecs1.stream()
                            .map(spec -> spec.getOptions() + ": " + spec.getOptionValue())
                            .collect(Collectors.joining(" | "));

                    productDTO.setSpecsString(specsString1);


                    detailUrl = "/product/keyboard/keyboardDetails?productNo=" + productNo;
                    productDTO.setUrl(detailUrl);
                    break;
            }
        }
        log.info("input 받은 값 확인 = {}", products );
        model.addAttribute("products",products );

        return "product/product/productList";

    }


    /**
     * Product details string.
     *
     * @param productNo the product code
     * @param model     the model
     * @return the string
     */
    @GetMapping("/laptop/laptopDetails")
    public String productLaptopDetails(@RequestParam(name = "productNo") int productNo,
                                 Model model, @RequestParam(value = "page",defaultValue = "1") int page) {
        log.info("1. 제품 세부정보 요청을 받았습니다.: {}", productNo);

        //customer 문의 부분
        List<ProductqList> productqList = customerService.getAllProductqList(productNo);
        model.addAttribute("productqList",productqList);
        model.addAttribute("memberNo", SecurityUtil.getUserNo());
        int page_size=4;
        int adjustPage=page-1;
        List<ProductqList> paginationList=paginationService.productpaginate2(productqList, adjustPage, page_size);
        int totalPages = (int) Math.ceil((double) productqList.size() / page_size);
        if(totalPages==0){totalPages=1;}
        System.out.println(paginationList.size());
        model.addAttribute("productqList",paginationList);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);

        // 제품 상세 정보 가져오기
        LaptopSpecDTO laptop = productService.getLaptopProductDetails(productNo);
        log.info("상세 제품 정보 결과 값 = {}, {}",laptop,laptop.getProductNo());

        model.addAttribute("productNo",laptop.getProductNo());
        model.addAttribute("laptop", laptop);

        return "product/laptop/laptopDetails";
    }

    @GetMapping("/keyboard/keyboardDetails")
    public String productKeyboardDetails(@RequestParam(name = "productNo") int productNo,
                                 Model model) {
        log.info("1. 제품 세부정보 요청을 받았습니다.: {}", productNo);

        //customer 문의 부분
        List<ProductqList> productqList = customerService.getAllProductqList(productNo);
        model.addAttribute("productqList",productqList);


        // 제품 상세 정보 가져오기
        KeyBoardSpecDTO keyboard = productService.getKeyboardProductDetails(productNo);

        model.addAttribute("productNo",keyboard.getProductNo());
        model.addAttribute("keyboard", keyboard);

        return "product/keyboard/keyboardDetails";
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

    @GetMapping("/test2")
    public String detailstest(@RequestParam("typeDetails") int typeDetails) {

        log.info("상품 타입 확인 = {}", typeDetails);
        switch (typeDetails) {
            case 1:
                crawlingService.getLaptopDetails(typeDetails);

                break;
            case 2:

                break;
            case 3:
                crawlingService. getkeyboardDetails(typeDetails);
                break;
        }

    return "product/product/productList";


    }

}



