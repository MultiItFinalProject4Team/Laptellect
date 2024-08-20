package com.multi.laptellect.main.service;

import com.multi.laptellect.main.model.dto.ProductMainDTO;
import com.multi.laptellect.main.model.mapper.MainMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

/**
 * Please explain the class!!
 *
 * @author : 이강석
 * @fileName : MainServiceImpl
 * @since : 2024-08-20
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MainServiceImpl implements MainService{
    private final MainMapper mainMapper;
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "main", key = "#p0", cacheManager = "mainPageCacheManager")
    public ArrayList<ProductMainDTO> findProduct(int typeNo) throws Exception{
        return mainMapper.findProductByTypeNo(typeNo);
    }
}
