package com.multi.laptellect.admin.product.model.mapper;

import com.multi.laptellect.common.model.PagebleDTO;
import com.multi.laptellect.product.model.dto.ProductDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;

/**
 * Please explain the class!!
 *
 * @author : 이우석
 * @fileName : AdminProductMapper
 * @since : 2024-08-19
 */

@Mapper
public interface AdminProductMapper {

    ArrayList<ProductDTO> getAllProducts(PagebleDTO pagebleDTO);

    long countAllProduct(PagebleDTO pagebleDTO);
}
