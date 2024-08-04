package com.multi.laptellect.product.model.dto;

import lombok.Data;

import java.sql.Date;

/**
 * 위시 리스트 객체
 *
 * @author : 이강석
 * @fileName : WishListDTO
 * @since : 2024-08-05
 */
@Data
public class WishListDTO {
    private int wishlistNo;
    private int productNo;
    private int memberNo;
    private Date createdAt;
}
