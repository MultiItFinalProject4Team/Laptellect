package com.multi.laptellect.product.controller;


import com.multi.laptellect.customer.dto.ProductqList;
import com.multi.laptellect.customer.service.CustomerService;
import com.multi.laptellect.customer.service.PaginationService;
import com.multi.laptellect.payment.model.dto.PaymentDTO;
import com.multi.laptellect.payment.model.dto.PaymentReviewDTO;
import com.multi.laptellect.payment.service.PaymentService;
import com.multi.laptellect.product.model.dto.KeyBoardSpecDTO;
import com.multi.laptellect.product.model.dto.ProductDTO;
import com.multi.laptellect.product.model.dto.laptop.LaptopSpecDTO;
import com.multi.laptellect.product.model.dto.mouse.MouseSpecDTO;
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
    private final CartService cartService;
    private final CrawlingService crawlingService;
    private final ProductService productService;
    private final CustomerService customerService;
    private final PaginationService paginationService;
    private final PaymentService paymentService;


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


    @GetMapping("/productList")
    public String cate(@RequestParam(name = "typeNo") int typeNo, Model model){

        Map<String, List<String>> cate = productService.productFilterSearch();



        log.info("컨트롤러 cate 데이터 확인 = {}", cate);

        model.addAttribute("cate", cate);
        model.addAttribute("typeNo", typeNo);
        return "/product/productList";

    }


    /**
     * Product details string.
     *
     * @param productNo the product code
     * @param model     the model
     * @return the string
     */

    @GetMapping("/productDetail")
    public String productLaptopDetails(@RequestParam(name = "productNo") int productNo,
                                 Model model, @RequestParam(value = "page",defaultValue = "1") int page) throws Exception {
        log.info("1. 제품 세부정보 요청을 받았습니다.: {}", productNo);

        // 장바구니 및 위시리스트 변수 선언
        ArrayList<Integer> carts = new ArrayList<>();
        ArrayList<Integer> wishlist = new ArrayList<>();
        PaymentDTO paymentDTO = new PaymentDTO();
        int memberNo = 0;
        String memberName = "";
        ProductDTO productDTO = productService.findProductByProductNo(String.valueOf(productNo));

        int typeNo = productDTO.getTypeNo();

        try {
            if (SecurityUtil.isAuthenticated()) {

                if(cartService.getCartList() != null) {
                    ArrayList<ProductDTO> cartInfo = cartService.getCartList().getProducts();
                    for(ProductDTO cartProduct : cartInfo) {
                        int productNo2 = cartProduct.getProductNo();
                        carts.add(productNo2);
                    }
                }

                wishlist = productService.getWishlistString();
                paymentDTO = paymentService.selectOrderItems(SecurityUtil.getUserNo(), productNo);
                memberNo = SecurityUtil.getUserNo();
                memberName = SecurityUtil.getUserDetails().getMemberName();
            }
            model.addAttribute("carts", carts);
            model.addAttribute("wishlist", wishlist);

            //customer 문의 부분
            List<ProductqList> productqList = customerService.getAllProductqList(productNo);
            model.addAttribute("productqList", productqList);
            model.addAttribute("memberName", memberName);
            model.addAttribute("memberNo", memberNo);

            switch(typeNo) {
                case 1 -> {
                    // 제품 상세 정보 가져오기
                    LaptopSpecDTO laptop = productService.getLaptopProductDetails(productNo);
                    log.info("상세 제품 정보 결과 값 = {}, {},{}",laptop,laptop.getProductNo(),laptop.getImage());


                    model.addAttribute("productNo",laptop.getProductNo());
                    model.addAttribute("laptop", laptop);

                    List<PaymentReviewDTO> paymentReviewDTOList = paymentService.findPaymentReviewsByProductNo(productNo);

                    model.addAttribute("typeNo", typeNo);
                    model.addAttribute("paymentDTO", paymentDTO);
                    model.addAttribute("paymentReviewDTOList", paymentReviewDTOList);
                    model.addAttribute("productDTO", productDTO );
                    model.addAttribute("memberNo", memberNo);
                    model.addAttribute("memberName", memberName);
                }
                case 2 -> {
                    // 제품 상세 정보 가져오기
                    MouseSpecDTO mouse = productService.getMouseProductDetails(productNo);

                    log.info("마우스 조회 목록 = {}",mouse);
                    log.info("마우스 조회 목록 = {}",mouse.getImage());

                    model.addAttribute("productNo",mouse.getProductNo());
                    model.addAttribute("mouse", mouse);

                    List<PaymentReviewDTO> paymentReviewDTOList = paymentService.findPaymentReviewsByProductNo(productNo);

                    model.addAttribute("typeNo", typeNo);
                    model.addAttribute("paymentDTO", paymentDTO);
                    model.addAttribute("paymentReviewDTOList", paymentReviewDTOList);
                    model.addAttribute("productDTO", productDTO);
                    model.addAttribute("memberNo", memberNo);
                    model.addAttribute("memberName", memberName);
                }
                case 3 -> {
                    KeyBoardSpecDTO keyboard = productService.getKeyboardProductDetails(productNo);

                    log.info("키보드 조회 목록 = {}",keyboard);
                    log.info("키보드 조회 목록 = {}",keyboard.getImage());

                    model.addAttribute("productNo", keyboard.getProductNo());
                    model.addAttribute("keyboard", keyboard);
                    List<PaymentReviewDTO> paymentReviewDTOList = paymentService.findPaymentReviewsByProductNo(productNo);

                    model.addAttribute("typeNo", typeNo);
                    model.addAttribute("paymentDTO", paymentDTO);
                    model.addAttribute("paymentReviewDTOList", paymentReviewDTOList);
                    model.addAttribute("productDTO", productDTO);
                    model.addAttribute("memberNo", memberNo);
                    model.addAttribute("memberName", memberName);
                }
            }
        } catch (Exception e) {
            log.error("상품 상세 조회 에러 = ", e);
        }

        return "product/productDetails";
    }

//    @GetMapping("/productDetail")
//    public String productKeyboardDetails(@RequestParam(name = "productNo") int productNo,
//                                         Model model) throws Exception {
//        log.info("1. 제품 세부정보 요청을 받았습니다.: {}", productNo);
//
//
//        // 장바구니 및 위시리스트 변수 선언
//        ArrayList<Integer> carts = new ArrayList<>();
//        ArrayList<Integer> wishlist = new ArrayList<>();
//        PaymentDTO paymentDTO = new PaymentDTO();
//        int memberNo = 0;
//        String memberName = "";
//
//        try {
//            if (SecurityUtil.isAuthenticated()) {
//
//                if (cartService.getCartList() != null) {
//                    ArrayList<ProductDTO> cartInfo = cartService.getCartList().getProducts();
//                    for (ProductDTO cartProduct : cartInfo) {
//                        int productNo2 = cartProduct.getProductNo();
//                        carts.add(productNo2);
//                    }
//                }
//
//                wishlist = productService.getWishlistString();
//                paymentDTO = paymentService.selectOrderItems(SecurityUtil.getUserNo(), productNo);
//                memberNo = SecurityUtil.getUserNo();
//                memberName = SecurityUtil.getUserDetails().getMemberName();
//            }
//            model.addAttribute("carts", carts);
//            model.addAttribute("wishlist", wishlist);
//
//
//            //customer 문의 부분
//            List<ProductqList> productqList = customerService.getAllProductqList(productNo);
//            model.addAttribute("productqList", productqList);
//            model.addAttribute("memberName", memberName);
//            model.addAttribute("memberNo", memberNo);
//
//            // 제품 상세 정보 가져오기
//            KeyBoardSpecDTO keyboard = productService.getKeyboardProductDetails(productNo);
//
//            log.info("키보드 조회 목록 = {}",keyboard);
//            log.info("키보드 조회 목록 = {}",keyboard.getImage());
//
//
//            model.addAttribute("productNo", keyboard.getProductNo());
//            model.addAttribute("keyboard", keyboard);
//        } catch (Exception e) {
//            log.error("상품 상세 조회 에러 = ", e);
//        }
//
//
//        ProductDTO productDTO = productService.findProductByProductNo(String.valueOf(productNo));
//        List<PaymentReviewDTO> paymentReviewDTOList = paymentService.findPaymentReviewsByProductNo(productNo);
//
//
//        model.addAttribute("typeNo", 3);
//        model.addAttribute("paymentDTO", paymentDTO);
//        model.addAttribute("paymentReviewDTOList", paymentReviewDTOList);
//        model.addAttribute("productDTO", productDTO);
//        model.addAttribute("memberNo", memberNo);
//        model.addAttribute("memberName", memberName);
//
//        return "product/productDetail";
//    }


//    @GetMapping("/productDetail")
//    public String productMouseDetails(@RequestParam(name = "productNo") int productNo,
//                                         Model model)throws Exception  {
//
//        // 장바구니 및 위시리스트 변수 선언
//        ArrayList<Integer> carts = new ArrayList<>();
//        ArrayList<Integer> wishlist = new ArrayList<>();
//        PaymentDTO paymentDTO = new PaymentDTO();
//        int memberNo = 0;
//        String memberName = "";
//
//        try {
//            if (SecurityUtil.isAuthenticated()) {
//
//                if (cartService.getCartList() != null) {
//                    ArrayList<ProductDTO> cartInfo = cartService.getCartList().getProducts();
//                    for (ProductDTO cartProduct : cartInfo) {
//                        int productNo2 = cartProduct.getProductNo();
//                        carts.add(productNo2);
//                    }
//                }
//
//                wishlist = productService.getWishlistString();
//                paymentDTO = paymentService.selectOrderItems(SecurityUtil.getUserNo(), productNo);
//                memberNo = SecurityUtil.getUserNo();
//                memberName = SecurityUtil.getUserDetails().getMemberName();
//            }
//            model.addAttribute("carts", carts);
//            model.addAttribute("wishlist", wishlist);
//
//
//            //customer 문의 부분
//            List<ProductqList> productqList = customerService.getAllProductqList(productNo);
//            model.addAttribute("productqList", productqList);
//            model.addAttribute("memberName", memberName);
//            model.addAttribute("memberNo", memberNo);
//
//
//
//            // 제품 상세 정보 가져오기
//            MouseSpecDTO mouse = productService.getMouseProductDetails(productNo);
//
//            log.info("마우스 조회 목록 = {}",mouse);
//            log.info("마우스 조회 목록 = {}",mouse.getImage());
//
//            model.addAttribute("productNo",mouse.getProductNo());
//            model.addAttribute("mouse", mouse);
//    } catch (Exception e) {
//        log.error("상품 상세 조회 에러 = ", e);
//    }
//
//
//        ProductDTO productDTO = productService.findProductByProductNo(String.valueOf(productNo));
//        List<PaymentReviewDTO> paymentReviewDTOList = paymentService.findPaymentReviewsByProductNo(productNo);
//
//
//        model.addAttribute("typeNo", 2);
//        model.addAttribute("paymentDTO", paymentDTO);
//        model.addAttribute("paymentReviewDTOList", paymentReviewDTOList);
//        model.addAttribute("productDTO", productDTO);
//        model.addAttribute("memberNo", memberNo);
//        model.addAttribute("memberName", memberName);
//
//        return "product/productDetail";
//    }

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
                crawlingService.getMouseDetails(typeDetails);

                break;
            case 3:
                crawlingService. getkeyboardDetails(typeDetails);
                break;
        }

    return "product/product/productList";


    }

}



