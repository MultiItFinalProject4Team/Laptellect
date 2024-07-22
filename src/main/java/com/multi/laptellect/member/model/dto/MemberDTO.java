package com.multi.laptellect.member.model.dto;

import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
public class MemberDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private int rowNum;
    private int memberNo;
    private String userName;
    private String email;
    private String password;
    private String loginType;
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
