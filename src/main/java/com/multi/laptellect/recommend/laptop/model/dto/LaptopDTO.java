package com.multi.laptellect.recommend.laptop.model.dto;

import lombok.Data;

@Data
public class LaptopDTO {
    private int tag_no;
    private String tag_data; //노트북 태그 데이터
    private String tag_etc; //주변기기 데이터
}