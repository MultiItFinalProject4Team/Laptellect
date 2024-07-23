package com.multi.laptellect.customer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class personalqImageDto {
    private String originName;
    private String uploadName;
    private String createdAt;
    private String updatedAt;
    private String referenceCode;
}
