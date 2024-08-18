package com.multi.laptellect.admin.visitor.model.dto;

import lombok.Data;

import java.sql.Timestamp;

/**
 * Please explain the class!!
 *
 * @author : 이강석
 * @fileName : AdminMemberDTO
 * @since : 2024-08-14
 */
@Data
public class AdminMemberDTO {
    private int memberNo;
    private int socialId;
    private String memberName;
    private String email;
    private String nickName;
    private String tel;
    private int point;
    private String role;
    private String loginType;
    private Timestamp createdAt;
    private String isActive;
    private Timestamp isActiveAt;
}