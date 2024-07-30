package com.multi.laptellect.member.model.dto;

import lombok.Data;

@Data
public class SocialDTO {
    private Long socialId;
    private int memberNo;
    private String externalId;
    private String email;
    private String nickName;
    private String loginType;
}
