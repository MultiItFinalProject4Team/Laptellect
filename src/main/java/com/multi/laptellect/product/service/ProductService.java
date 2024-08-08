package com.multi.laptellect.product.service;

import com.multi.laptellect.product.model.dto.LaptopDetailsDTO;
import com.multi.laptellect.product.model.dto.ProductDTO;
import com.multi.laptellect.product.model.dto.SpecDTO;
import com.multi.laptellect.product.model.dto.WishlistDTO;
import com.multi.laptellect.product.model.dto.laptop.LaptopSpecDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

/**
 * The interface Product service.
 */
public interface ProductService {

    /**
     * 상품의 기본 정보 db 저장
     *
     * @param prodctList 상품 리스트
     * @param typeNo     상품의 타입 지정
     */
    void saveProductsToDB(List<ProductDTO> prodctList, int typeNo) throws Exception;

    /**
     * 상품리스트 조회
     *
     * @param pageNumber 페이지
     * @param pageSize   페이지 크기
     * @return the stored products
     */
    List<ProductDTO> getStoredProducts(Integer typeNo,int pageNumber, int pageSize);

    /**
     * 모든 상품의 수량 확인
     *
     * @return the total products
     */
    int getTotalProducts();

    /**
     * Gets type by product.
     *
     * @param typeNo the type no
     * @return the type by product
     */
    List<ProductDTO> getTypeByProduct(int typeNo);


    /**
     * Gets laptop product details.
     *
     * @param productNo the product code
     * @return the laptop product details
     */
    LaptopSpecDTO getLaptopProductDetails(int productNo);


    /**
     * 위시리스트 INSERT
     *
     * @param productNo 회원번호
     * @return 성공 여부 Bool
     * @throws Exception the exception
     */
    int processWishlist(List<Integer> productNo) throws Exception;

    /**
     * Gets product by type.
     *
     * @param typeNo the type no
     * @return the product by type
     */
    int getProductByType(int typeNo);

    /**
     * Gets product spec.
     *
     * @param productNo the product no
     * @return the product spec
     */
    List<SpecDTO> getProductSpec(int productNo);

    /**
     * Filter specs list.
     *
     * @param productNo     the product no
     * @param neededOptions the needed options
     * @return the list
     */
    List<SpecDTO> filterSpecs(int productNo, Set<String> neededOptions);

    /**
     * Gets wishlist.
     *
     * @param pageable the pageable
     * @return the wishlist
     * @throws Exception the exception
     */
    Page<WishlistDTO> getWishlist(Pageable pageable) throws Exception;

    ProductDTO findProductByProductNo(String productNo) throws Exception;

    LaptopSpecDTO getLaptopSpec(int productNo, List<LaptopDetailsDTO> laptopDetails);


}
