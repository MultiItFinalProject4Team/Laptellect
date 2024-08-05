package com.multi.laptellect.product.service;

import com.multi.laptellect.product.model.dto.LaptopDetailsDTO;
import com.multi.laptellect.product.model.dto.ProductDTO;
import com.multi.laptellect.product.model.dto.WishlistDTO;
import com.multi.laptellect.product.model.dto.laptop.LaptopSpecDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {

    void saveProductsToDB(List<ProductDTO> prodctList, int typeNo) throws Exception;

    List<ProductDTO> getStoredProducts(int pageNumber, int pageSize);

    int getTotalProducts();

    LaptopSpecDTO getProductByCode(String pcode);

    List<ProductDTO> getTypeByProduct(int typeNo);

    void getImgae(String referenceCode);

    List<LaptopDetailsDTO> getLaptopProductDetails(String productCode);

    /**
     * 위시리스트 INSERT
     *
     * @param productNo 회원번호
     * @return 성공 여부 Bool
     * @throws Exception the exception
     */
    int processWishlist(List<Integer> productNo) throws Exception;


    Page<WishlistDTO> getWishlist(Pageable pageable) throws Exception;

}
