package com.multi.laptellect.main.service;

import com.multi.laptellect.main.model.dto.ProductMainDTO;

import java.util.ArrayList;

/**
 * Please explain the class!!
 *
 * @author : 이강석
 * @fileName : MainService
 * @since : 2024-08-20
 */
public interface MainService {
    ArrayList<ProductMainDTO> findProduct(int typeNo) throws Exception;
}
