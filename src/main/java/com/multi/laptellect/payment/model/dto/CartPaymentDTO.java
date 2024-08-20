package com.multi.laptellect.payment.model.dto;

import lombok.Data;
import com.multi.laptellect.product.model.dto.ProductDTO;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CartPaymentDTO {
    private int productNo;
    private List<ProductDTO> products;
    private String imPortId;
    private BigDecimal totalAmount;
    private String usedPoints;
    private int addressId;
}