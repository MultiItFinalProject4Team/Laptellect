package com.multi.laptellect.product.model.dto;


import lombok.Data;

import java.sql.Timestamp;

@Data
public class ImageDTO {

    private int imageNo;
    private String referenceCode;
    private String originName;
    private String uploadName;
    private Timestamp createdAt;
    private Timestamp updatedAt;

}
