package com.multi.laptellect.product.service;

import com.multi.laptellect.product.model.dto.ProductDTO;
import com.multi.laptellect.product.model.dto.ProductInfo;
import com.multi.laptellect.product.model.dto.ProductTypeDTO;
import com.multi.laptellect.product.model.mapper.ProductMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;


    @Override
    @Transactional
    public void saveProductsToDB(List<ProductInfo> productList) {

        List<ProductDTO> productDTOList = new ArrayList<>();

        LocalDateTime now = LocalDateTime.now();

        for (ProductInfo productInfo : productList) {
            ProductDTO productDTO = new ProductDTO();
            productDTO.setProductName(productInfo.getProductName());
            productDTO.setPrice(Integer.parseInt(productInfo.getPrice()));
            productDTO.setProductCode(productInfo.getPcode());
            productDTO.setImage(productInfo.getImageUrl());
            productDTO.setCreatedAt(Timestamp.valueOf(now));
            //하드코딩
            productDTO.setTypeNo(1);

            productDTOList.add(productDTO);
        }

        // 중복 확인 및 데이터베이스 삽입
        for (ProductDTO productDTO : productDTOList) {
            int count = productMapper.countByProductCode(productDTO.getProductCode());
            if (count == 0) {
                productMapper.insertProduct(productDTO);

                String code = "P" + productDTO.getProductNo();

                productDTO.setReferenceCode(code);


                productMapper.updateReferenceCode(productDTO);
                //이미지 처리

            } else {
                log.info("제품코드는: " + productDTO.getProductCode() + " 입니다.");
            }
        }

    }





    //상품 전체 조회
    @Override
    @Transactional
    public List<ProductInfo> getStoredProducts() {

        List<ProductDTO> productDTOList = productMapper.getAllProducts();
        List<ProductInfo> productInfoList = new ArrayList<>();

        for (ProductDTO productDTO : productDTOList) {
            ProductInfo productInfo = new ProductInfo();


            productInfo.setPcode(productDTO.getProductCode());
            productInfo.setProductName(productDTO.getProductName());
            productInfo.setPrice(String.valueOf(productDTO.getPrice()));
            productInfo.setImageUrl(productDTO.getImage());
            productInfoList.add(productInfo);



        }

        return productInfoList;
    }


    @Override
    public List<ProductTypeDTO> getAllProductTypes() {
        return  productMapper.findTypeNoByName();
    }



    @Override
    public ProductInfo getProductByCode(String pcode) {
        ProductDTO productDTO = productMapper.getProductByCode(pcode);
        if (productDTO == null) {
            return null;
        }

        ProductInfo productInfo = new ProductInfo();
        productInfo.setPcode(productDTO.getProductCode());
        productInfo.setProductName(productDTO.getProductName());
        productInfo.setPrice(String.valueOf(productDTO.getPrice()));
        productInfo.setImageUrl(productDTO.getReferenceCode());
        return productInfo;
    }

}
