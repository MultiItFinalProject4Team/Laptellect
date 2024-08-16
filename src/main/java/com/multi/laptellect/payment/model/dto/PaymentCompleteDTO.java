package com.multi.laptellect.payment.model.dto;

import lombok.Data;
import java.util.List;

@Data
public class PaymentCompleteDTO {
    private List<ProductInfo> products;
    private int totalQuantity;
    private int totalPrice;
    private int discountAmount;
    private int usedPoints;
}
