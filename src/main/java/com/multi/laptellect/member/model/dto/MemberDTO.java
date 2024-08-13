package com.multi.laptellect.member.model.dto;

import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
public class MemberDTO implements Serializable {
    private static final long serialVersionUID = 804705291130202933L;

    private int rowNum;
    private int memberNo;
    private String memberName;
    private String email;
    private String password;
    private String loginType;
    private String nickName;
    private String tel;
    private int point;
    private String role;
    private Timestamp createDate;
    private Timestamp modifyDate;
    private String isActive;
}
