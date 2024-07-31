package com.multi.laptellect.customer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ImageDto {
    private String originName;
    private String uploadName;
    private Date createdAt;
    private Date updatedAt;
    private String referenceCode;
}
