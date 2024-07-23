package com.multi.laptellect.product.service;


import com.multi.laptellect.product.model.dto.ProductDTO;
import com.multi.laptellect.product.model.mapper.ProductMapper;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class productServiceImpl implements ProductService{

    @Autowired
    private final ProductMapper productMapper;

    @Override
    public int countByProductCode(String productCode) {
        return productMapper.countByProductCode(productCode);
    }


    @Override
    @Transactional
    public void saveProduct(List<ProductDTO> productList) {
        for(ProductDTO productDTO : productList) {
            if (productMapper.countByProductCode(productDTO.getProductCode()) == 0){
                productMapper.insertProduct(productDTO);
            }
        }
    }


    @Override
    public List<ProductDTO> getAllProducts() {
        return productMapper.getAllProducts();
    }

    @Override
    public ProductDTO getProductByCode(String productCode) {
        return productMapper.getProductByCode(productCode);
    }


}
