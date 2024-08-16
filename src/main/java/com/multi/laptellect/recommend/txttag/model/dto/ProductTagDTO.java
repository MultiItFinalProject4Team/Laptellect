package com.multi.laptellect.recommend.txttag.model.dto;

import lombok.Data;

@Data
public class ProductTagDTO { // 상품과 태그를 연결하는 DTO
    private int productNo;
    private int tagNo;
}
