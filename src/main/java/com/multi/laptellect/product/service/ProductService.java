package com.multi.laptellect.product.service;

import com.multi.laptellect.product.model.dto.LaptopDetailsDTO;
import com.multi.laptellect.product.model.dto.ProductDTO;
import com.multi.laptellect.product.model.dto.laptop.LaptopSpecDTO;

import java.util.List;

public interface ProductService {

    void saveProductsToDB(List<ProductDTO> prodctList, int typeNo) throws Exception;

    List<ProductDTO> getStoredProducts();

    LaptopSpecDTO getProductByCode(String pcode);

    List<ProductDTO> getTypeByProduct(int typeNo);

    void getImgae(String referenceCode);

    List<LaptopDetailsDTO> getLaptopProductDetails(String productCode);


}
