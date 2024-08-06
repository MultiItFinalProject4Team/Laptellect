package com.multi.laptellect.product.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

/**
 * Please explain the class!!
 *
 * @author : 이강석
 * @fileName : ProductCart
 * @since : 2024-08-06
 */
@Getter
@Setter
public class ProductCart {
    private ArrayList<ProductDTO> products;
    private int totalQuantity;
}
