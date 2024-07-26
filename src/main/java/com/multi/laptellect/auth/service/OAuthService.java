package com.multi.laptellect.auth.service;

import com.multi.laptellect.member.model.dto.KakaoDTO;

public interface OAuthService {
    String getKakaoAccessToken (String code);

    KakaoDTO getKaKaoProfileInfo(String accessToken);

    void processKakaoUser(KakaoDTO kakaoDTO);
}
