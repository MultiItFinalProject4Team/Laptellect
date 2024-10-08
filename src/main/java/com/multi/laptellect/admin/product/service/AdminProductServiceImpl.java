package com.multi.laptellect.admin.product.service;

import com.multi.laptellect.admin.product.model.mapper.AdminProductMapper;
import com.multi.laptellect.common.model.PagebleDTO;
import com.multi.laptellect.product.model.dto.ProductDTO;
import com.multi.laptellect.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

/**
 * Please explain the class!!
 *
 * @author : 이우석
 * @fileName : AdminProductServiceImlp
 * @since : 2024-08-19
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminProductServiceImpl implements AdminProductService{

    private final AdminProductMapper adminProductMapper;

    private final RedisUtil redisUtil;
    private final String REDIS_KEY_PREFIX = "deleted_product:";


    @Override
    public Page<ProductDTO> getProductList(PagebleDTO pagebleDTO) throws Exception {
        log.debug("파라미터 확인 = {}", pagebleDTO);

        ArrayList<ProductDTO> products = adminProductMapper.getAllProducts(pagebleDTO);
        log.debug("서비스 파라미터 확인 = {}",products);
        long total = adminProductMapper.countAllProduct(pagebleDTO);
        log.debug("총수량 확인 = {}",total);

        return new PageImpl<>(products, pagebleDTO, total);

    }

    @Override
    @Transactional
    public int deleteProduct(int productNo) throws Exception{
        log.debug("ProductServiceImpl 삭제정보 : {}",productNo);

        int typeNo = adminProductMapper.findProductTypeNoByProducctNo(productNo);
        redisUtil.deleteData("product::" + productNo);
        redisUtil.deleteData("main::" + typeNo);

        return adminProductMapper.deleteProduct(productNo);
    }

    @Override
    public String findUploadName(int productNo) {

       return adminProductMapper.findUploadName(productNo);
    }
}
