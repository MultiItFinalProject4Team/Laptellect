package com.multi.laptellect.member.model.dto;

import lombok.Data;

@Data
public class KakaoDTO {
    private Long socialId;
    private int memberNo;
    private Long externalId;
    private String email;
    private String nickName;
    private String loginType = "kakao";
}
