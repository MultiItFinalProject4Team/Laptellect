package com.multi.laptellect.payment.model.dto;

import com.multi.laptellect.member.model.dto.AddressDTO;
import lombok.Data;

@Data
public class PaymentDetailDTO {
    private PaymentDTO paymentDTO;
    private AddressDTO addressDTO;
}
