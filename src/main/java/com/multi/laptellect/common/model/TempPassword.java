package com.multi.laptellect.common.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TempPassword {
    private int memberNo;
    private String tempPw;
    private Boolean isUse;
    private LocalDateTime expDate;
}
