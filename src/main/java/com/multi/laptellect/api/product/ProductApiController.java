package com.multi.laptellect.api.product;

import com.multi.laptellect.product.model.dto.ProductDTO;
import com.multi.laptellect.product.model.dto.ProductSearchDTO;
import com.multi.laptellect.product.model.dto.SpecDTO;
import com.multi.laptellect.product.model.dto.WishlistDTO;
import com.multi.laptellect.product.service.CartService;
import com.multi.laptellect.product.service.ProductService;
import com.multi.laptellect.util.PaginationUtil;
import com.multi.laptellect.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 상품 API 처리 컨트롤러
 *
 * @author : 이강석
 * @fileName : ProductApiController
 * @since : 2024-08-05
 */
@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/api/product")
public class ProductApiController {
    private final ProductService productService;
    private final CartService cartService;

    /**
     * 위시리스트 추가
     *
     * @param productNo 상품 번호
     * @return the boolean
     */
    @ResponseBody
    @PostMapping("/process-wishlist")
    public int processWishlist(@RequestParam(name = "productNo") List<Integer> productNo) {
        int result = 500;
        try {
            result = productService.processWishlist(productNo);
        } catch (Exception e) {
            log.error("위시리스트 등록 실패");
        }
        return result;
    }

    @GetMapping("/get-wishlist")
    public String getWishlist(Model model, @PageableDefault(size = 5) Pageable pageable) {
        try {
            Page<WishlistDTO> wishlist = productService.getWishlist(pageable);

            int page = pageable.getPageNumber();
            int size = pageable.getPageSize();
            long total = wishlist.getTotalElements();

            model.addAttribute("wishlist", wishlist);
            model.addAttribute("total", total);
            model.addAttribute("page", page);
            model.addAttribute("size", size);
        } catch (Exception e) {
            log.error("위시리스트 조회 실패 = ", e);
        }
        return "member/wishlist/wishlist";
    }

    /**
     *
     *
     * @param searchDTO 검색,정렬,페이징 이 담겨있는 dto
     * @return
     */
    @GetMapping("/product-list")
    public String getProductList(Model model,
                                 Pageable pageable,
                                 ProductSearchDTO searchDTO) {

        searchDTO.setPage(pageable.getPageNumber());
        searchDTO.setSize(pageable.getPageSize());


        log.info("카테고리 파라미터 확인 = {},",searchDTO);




        // Page<> : 페이징된 결과와 관련 정보를 함께 제공하는 Spring Data JPA의 강력한 도구
        Page<ProductDTO> productPage = productService.searchProducts(searchDTO);
        // getContent() : 페이징된 데이터를 얻을 수 있음

        log.info( "페이징 데이터 = {}", productPage);

        ArrayList<Integer> carts = new ArrayList<>();
        ArrayList<Integer> wishlist = new ArrayList<>();

            String detailUrl;

        try {


//            int displayPages = 10;
            int currentPage = pageable.getPageNumber();
            int totalPages = productPage.getTotalPages() -1 ;


//            int startPage = ((currentPage - 1) / displayPages) * displayPages + 1;
//            int endPage = Math.min(startPage + displayPages - 1, totalPages);

            int startPage = PaginationUtil.getStartPage(productPage, 9);
            int endPage = PaginationUtil.getEndPage(productPage, 9);


            model.addAttribute("page", currentPage );
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("startPage", startPage);
            model.addAttribute("endPage", endPage);
            log.info( "페이징 데이터 = {},{}",currentPage, totalPages);
            log.info("시작페이지 = {} , 종료페이지 = {}", startPage, endPage);


            int pageSize = pageable.getPageSize();
            int totalItems = (int) productPage.getTotalElements();

            int loadedItems = currentPage * pageSize;
            if (loadedItems > totalItems) {
                loadedItems = totalItems; // 총 항목 수를 초과하지 않도록 조정
            }

            log.info("로드된 페이지당 제품 수량: {}", loadedItems);

            List<ProductDTO> products = productPage.getContent();
            log.info("현재 페이지에 로드된 제품 수: {}", products.size());

            // 사용자가 로그인한 상태인지 확인하여 로그인한 상태면 위시리스트와 장바구니 정보가 담긴 model을 넘김
            if (SecurityUtil.isAuthenticated()) {

                if(cartService.getCartList() != null) {
                    ArrayList<ProductDTO> cartInfo = cartService.getCartList().getProducts();
                    for(ProductDTO cartProduct : cartInfo) {
                        int productNo = cartProduct.getProductNo();
                        carts.add(productNo);
                    }
                }

                wishlist = productService.getWishlistString();
            }
            model.addAttribute("carts", carts);
            model.addAttribute("wishlist", wishlist);

            for (ProductDTO productDTO : productPage) {
                int productNo = productDTO.getProductNo();



                switch (searchDTO.getTypeNo()) {
                    case 1: // 노트북
                        log.info("laptop Get Spec = {}", searchDTO.getTypeNo());
//                        Set<String> neededOptions = Set.of("운영체제(OS)", "제조사", "램 용량", "저장 용량", "해상도", "화면 크기", "GPU 종류", "코어 수", "CPU 넘버");
                        Set<String> neededOptions = Set.of("운영체제(OS)", "제조사", "램 용량", "저장 용량", "해상도", "화면 크기", "CPU 넘버");
                        List<SpecDTO> filteredSpecs = productService.filterSpecs(productNo, neededOptions);
                        productDTO.setSpecs(filteredSpecs);
                        log.info("필터링된 Spec 값 전달 확인 ={}", filteredSpecs);

                        String specsString = filteredSpecs.stream()
                                .map(spec -> spec.getOptions() + ": " + spec.getOptionValue())
                                .collect(Collectors.joining(" | "));

                        productDTO.setSpecsString(specsString);

                        detailUrl = "/product/productDetail?productNo=" + productNo;
                        productDTO.setUrl(detailUrl);

                        break;
                    case 2: // 마우스
                        log.info("Mouse Get Spec = {}", searchDTO.getTypeNo());
                        Set<String> neededOptions2 = Set.of("최대 감도(DPI)", "응답 속도(M)", "무게(M)", "인터페이스(M)");
//                        Set<String> neededOptions2 = Set.of("최대 감도(DPI)", "응답 속도(M)", "가로(M)", "세로(M)", "높이(M)", "무게(M)", "인터페이스(M)");
                        List<SpecDTO> filteredSpecs2 = productService.filterSpecs(productNo, neededOptions2);
                        productDTO.setSpecs(filteredSpecs2);
                        log.info("필터링된 Spec 값 전달 확인 ={}", filteredSpecs2);

                        String specsString2 = filteredSpecs2.stream()
                                .map(spec -> spec.getOptions() + ": " + spec.getOptionValue())
                                .collect(Collectors.joining(" | "));

                        productDTO.setSpecsString(specsString2);


                        detailUrl = "/product/productDetail?productNo=" + productNo;
                        productDTO.setUrl(detailUrl);
                        break;
                    case 3: // 키보드
                        log.info("keyboard Get Spec = {}", searchDTO.getTypeNo());
                        Set<String> neededOptions1 = Set.of("제조사", "연결 방식", "사이즈", "인터페이스", "접점 방식", "스위치");
//                        Set<String> neededOptions1 = Set.of("제조사", "연결 방식", "사이즈", "인터페이스", "접점 방식", "스위치", "가로", "세로");
                        List<SpecDTO> filteredSpecs1 = productService.filterSpecs(productNo, neededOptions1);
                        productDTO.setSpecs(filteredSpecs1);
                        log.info("필터링된 Spec 값 전달 확인 ={}", filteredSpecs1);

                        String specsString1 = filteredSpecs1.stream()
                                .map(spec -> spec.getOptions() + ": " + spec.getOptionValue())
                                .collect(Collectors.joining(" | "));

                        productDTO.setSpecsString(specsString1);


                        detailUrl = "/product/productDetail?productNo=" + productNo;
                        productDTO.setUrl(detailUrl);
                        break;
                }

            }
        } catch (Exception e) {
            log.error("Product find fail = ", e);
            model.addAttribute("wishlist", wishlist); // 예외 발생 시에도 빈 리스트 추가
        }

        model.addAttribute("size", searchDTO.getSize());
        model.addAttribute("sort", searchDTO.getSort());
        model.addAttribute("products", productPage);
        model.addAttribute("typeNo", searchDTO.getTypeNo());
        model.addAttribute("keyword", searchDTO.getKeyword());

        return "product/product/productList";
    }
}
