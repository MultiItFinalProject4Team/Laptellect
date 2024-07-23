package com.multi.laptellect.product.model.mapper;

import com.multi.laptellect.product.model.dto.ProductDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProductMapper {


    int countByProductCode(@Param("productCode") String productCode);

    void insertProduct(ProductDTO productDTO);

    List<ProductDTO> getAllProducts();

    ProductDTO getProductByCode(@Param("productCode") String productCode);

}
