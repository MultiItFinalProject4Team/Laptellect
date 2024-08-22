package com.multi.laptellect.admin.product.service;

import com.multi.laptellect.common.model.PagebleDTO;
import com.multi.laptellect.product.model.dto.ProductDTO;
import org.springframework.data.domain.Page;

/**
 * Please explain the class!!
 *
 * @author : 이우석
 * @fileName : AdminProductService
 * @since : 2024-08-19
 */
public interface AdminProductService {


    Page<ProductDTO> getProductList(PagebleDTO pagebleDTO) throws Exception;


    int deleteProduct(int productNo);

    String findUploadName(int productNo);
}
