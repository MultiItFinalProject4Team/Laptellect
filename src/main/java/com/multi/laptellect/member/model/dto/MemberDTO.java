package com.multi.laptellect.member.model.dto;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class MemberDTO {
    private int rowNum;
    private int memberNo;
    private String userName;
    private String email;
    private String password;
    private String tempPassword;
    private Boolean tempPasswordIsUse;
    private Timestamp tempExpDate;
    private String nickName;
    private String tel;
    private int point;
    private String role;
    private Timestamp createDate;
    private Timestamp modifyDate;
}
