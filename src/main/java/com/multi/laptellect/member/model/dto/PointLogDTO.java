package com.multi.laptellect.member.model.dto;

import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 포인트 내역 조회
 *
 * @author : 이강석
 * @fileName : PointDTO
 * @since : 2024-08-04
 */
@Data
public class PointLogDTO {
    private int rowNum;
    private String imPortId;
    private int paymentPointChange;
    private String paymentPointInfo ;
    private Date createdAt;

    public String getFormatCreatedAt() {
        SimpleDateFormat formatCreateAt = new SimpleDateFormat("yyyy.MM.dd");
        return formatCreateAt.format(createdAt);
    }
}
