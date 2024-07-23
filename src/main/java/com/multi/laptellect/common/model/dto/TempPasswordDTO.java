package com.multi.laptellect.common.model.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TempPasswordDTO {
    private int memberNo;
    private String tempPw;
    private Boolean isUse;
    private LocalDateTime expDate;
}
