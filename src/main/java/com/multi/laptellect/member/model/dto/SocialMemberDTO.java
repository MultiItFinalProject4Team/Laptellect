package com.multi.laptellect.member.model.dto;

import lombok.Data;

@Data
public class SocialMemberDTO {

    private int memberNo;
    private String userName;
    private String email;
    private String password;
    private String loginType;
    private String nickName;
    private String tel;
    private String role;
}
