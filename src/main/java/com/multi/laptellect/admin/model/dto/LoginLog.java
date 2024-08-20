package com.multi.laptellect.admin.model.dto;

import lombok.Data;

import java.util.Date;

/**
 * Please explain the class!!
 *
 * @author : 이강석
 * @fileName : LoginLog
 * @since : 2024-08-17
 */
@Data
public class LoginLog {
    private int memberNo;
    private String loginIp;
    private String userAgent;
    private Date createdAt;
}
