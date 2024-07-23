package com.multi.laptellect.product.service;

import com.multi.laptellect.product.model.dto.ProductDTO;

import java.util.List;

public interface ProductService {
    int countByProductCode(String productCode);

    void saveProduct(List<ProductDTO> productList);

    List<ProductDTO> getAllProducts();

    ProductDTO getProductByCode(String productCode);


}
