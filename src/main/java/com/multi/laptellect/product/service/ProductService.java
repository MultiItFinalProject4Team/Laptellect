package com.multi.laptellect.product.service;

import com.multi.laptellect.product.model.dto.ProductInfo;
import com.multi.laptellect.product.model.dto.ProductTypeDTO;

import java.util.List;

public interface ProductService {

    void saveProductsToDB(List<ProductInfo> prodctList);

    List<ProductTypeDTO> getAllProductTypes();

    List<ProductInfo> getStoredProducts();

    ProductInfo getProductByCode(String pcode);
}
