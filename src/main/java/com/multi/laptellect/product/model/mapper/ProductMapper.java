package com.multi.laptellect.product.model.mapper;

import com.multi.laptellect.product.model.dto.ProductDTO;
import com.multi.laptellect.product.model.dto.ProductTypeDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProductMapper {

    void insertProduct(ProductDTO product);

    int countByProductCode(String productCode);

    List<ProductDTO> getAllProducts();

    ProductDTO getProductByCode(String productCode);

    List<ProductTypeDTO> findTypeNoByName();


    void updateReferenceCode(ProductDTO productDTO);



}
