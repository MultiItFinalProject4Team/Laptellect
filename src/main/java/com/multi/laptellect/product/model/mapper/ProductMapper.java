package com.multi.laptellect.product.model.mapper;

import com.multi.laptellect.product.model.dto.ImageDTO;
import com.multi.laptellect.product.model.dto.ProductDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProductMapper {

    void insertProduct(ProductDTO product); //크롤링 검색 후 상품등록

    int countByProductCode(String productCode); //상품코드 계수

    List<ProductDTO> getAllProducts();

    ProductDTO getProductByCode(String productCode);


    List<ProductDTO> getTypeByProduct(int typeNo);

    void updateReferenceCode(ProductDTO productDTO);

    void inputImage(ImageDTO imageDTO);

    List<String> getImage(String referenceCode);




}
