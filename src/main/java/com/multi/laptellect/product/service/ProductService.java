package com.multi.laptellect.product.service;

import com.multi.laptellect.product.model.dto.ProductDTO;
import com.multi.laptellect.product.model.dto.ProductInfo;

import java.util.List;

public interface ProductService {

    void saveProductsToDB(List<ProductInfo> prodctList, int typeNo) throws Exception;

    List<ProductDTO> getStoredProducts();

    ProductInfo getProductByCode(String pcode);

    List<ProductDTO> getTypeByProduct(int typeNo);

    List<String> getImgae(String referenceCode);
}
