package com.multi.laptellect.common.model.dto;

import lombok.Data;

@Data
public class EmailDTO {
    private String receiveAddress;
    private String mailTitle;
    private String mailContent;
}
