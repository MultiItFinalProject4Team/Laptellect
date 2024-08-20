package com.multi.laptellect.main.model.mapper;

import com.multi.laptellect.main.model.dto.ProductMainDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;

/**
 * Please explain the class!!
 *
 * @author : 이강석
 * @fileName : MainMapper
 * @since : 2024-08-20
 */
@Mapper
public interface MainMapper {
    ArrayList<ProductMainDTO> findProductByTypeNo(int typeNo);
}
