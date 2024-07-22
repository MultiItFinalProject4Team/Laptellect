package com.multi.laptellect.product.service;

import com.multi.laptellect.product.model.dto.ProductInfo;
import org.springframework.stereotype.Service;

@Service
public class ProductDetailsService {

    public static void getProductDetails(ProductInfo productInfo){

        String url = "https://prod.danawa.com/info/ajax/getProductDescription.ajax.php";
        String referer = "https://prod.danawa.com/info/?pcode=" + productInfo.getPcode() + "&cate=112758";
    }


}
