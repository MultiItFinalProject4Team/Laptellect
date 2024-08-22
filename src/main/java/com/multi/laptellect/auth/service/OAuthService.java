package com.multi.laptellect.auth.service;

import com.multi.laptellect.member.model.dto.SocialDTO;

/**
 * The interface O auth service.
 */
public interface OAuthService {
    /**
     * 카카오 엑세스 토큰 가져오기 위한 메서드
     *
     * @param code 카카오 로그인 API에서 반환되는 코드 값
     * @return 카카오 엑세스 토큰 값 return
     */
    String getKakaoAccessToken (String code);

    /**
     * 엑세스 토큰을 사용하여 카카오에서 프로필 정보를 받아오기 위한 메서드
     *
     * @param accessToken 카카오 엑세스 토큰
     * @return KakaoDTO에 담아 프로필 정보 리턴
     */
    SocialDTO getKaKaoProfileInfo(String accessToken);

    /**
     * 카카오 유저에 대한 로그인 및 회원가입 처리
     *
     * @param socialDTO 카카오 프로필 정보가 담긴 DTO
     */
    String processKakaoUser(SocialDTO socialDTO);

    /**
     * 구글 엑세스 토큰 발급을 위한 메서드
     *
     * @param code 구글 로그인 시 code값을 리턴
     * @return 엑세스 토큰 값 리턴
     */
    String getGoogleAccessToken(String code);

    /**
     * 구글 사용자 정보를 받아오기 위한 메서드
     *
     * @param accessToken 엑세스 토큰 값
     * @return 구글에서 받아온 프로필 정보를 SocialDTO에 담아 리턴
     */
    SocialDTO getGoogleProfileInfo(String accessToken);

    String processGoogleUser(SocialDTO SocialDTO);
}
