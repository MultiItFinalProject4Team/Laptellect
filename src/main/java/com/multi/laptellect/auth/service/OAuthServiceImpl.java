package com.multi.laptellect.auth.service;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.multi.laptellect.auth.model.mapper.AuthMapper;
import com.multi.laptellect.config.api.KakaoConfig;
import com.multi.laptellect.member.model.dto.CustomUserDetails;
import com.multi.laptellect.member.model.dto.KakaoDTO;
import com.multi.laptellect.member.model.dto.MemberDTO;
import com.multi.laptellect.util.CodeGenerator;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
@Slf4j
@RequiredArgsConstructor
public class OAuthServiceImpl implements OAuthService{
    private final KakaoConfig kakaoConfig;
    private final AuthMapper authMapper;

    @Override
    public String getKakaoAccessToken(String code) {
        String KakaoRestApiKey = kakaoConfig.getKakaoRestApiKey();
        String KakaoSecretKey = kakaoConfig.getKakaoSecretKey();
        String redirectURL = kakaoConfig.getKakaoRedirectUri();

        String tokenURL = "https://kauth.kakao.com/oauth/token";

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", KakaoRestApiKey);
        params.add("redirect_uri", redirectURL);
        params.add("client_secret", KakaoSecretKey);
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                tokenURL, // https://{요청할 서버 주소}
                HttpMethod.POST, // 요청할 방식
                kakaoTokenRequest, // 요청할 때 보낼 데이터
                String.class // 요청 시 반환되는 데이터 타입
        );

        String responseBody = response.getBody();
        JsonElement element = JsonParser.parseString(responseBody);

        String accessToken = element.getAsJsonObject().get("access_token").getAsString();
        // String refreshToken = element.getAsJsonObject().get("refresh_token").getAsString();

        return accessToken;
    }

    @Override
    public KakaoDTO getKaKaoProfileInfo(String accessToken) {
        String KakaoRestApiKey = kakaoConfig.getKakaoRestApiKey();
        String KakaoSecretKey = kakaoConfig.getKakaoSecretKey();
        String redirectURL = kakaoConfig.getKakaoRedirectUri();

        String profileURL = "https://kapi.kakao.com/v2/user/me";

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<String> kakaoTokenRequest = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                profileURL, // https://{요청할 서버 주소}
                HttpMethod.GET, // GET 메소드 사용
                kakaoTokenRequest, // 요청할 때 보낼 데이터
                String.class // 요청 시 반환되는 데이터 타입
        );

        JsonObject jsonObject = JsonParser.parseString(response.getBody()).getAsJsonObject();
        long id = jsonObject.get("id").getAsLong();
        String nickname = jsonObject.get("properties").getAsJsonObject().get("nickname").getAsString();
        String email = jsonObject.get("kakao_account").getAsJsonObject().get("email").getAsString();


        log.info("카카오 JSON 리턴 = {}", jsonObject);

        KakaoDTO kakaoDTO = new KakaoDTO();
        kakaoDTO.setExternalId(id);
        kakaoDTO.setEmail(email);
        kakaoDTO.setNickName(nickname);

        log.info("카카오 파싱 = {}", kakaoDTO);

        return kakaoDTO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void processKakaoUser(KakaoDTO kakaoDTO) {
        MemberDTO memberDTO = new MemberDTO();

        Long externalId = kakaoDTO.getExternalId();
        KakaoDTO kakaoUser = authMapper.findSocialMemberByExternalId(externalId);

        log.debug("카카오 로그인 실행 = {}", externalId);
        if(kakaoUser != null) {
            int memberNo = kakaoUser.getMemberNo();
            memberDTO = authMapper.findMemberByMemberNo(memberNo);
            log.info("존재하는 사용자 = {}", memberDTO);
        } else {
            memberDTO.setMemberName(CodeGenerator.createRandomString(5));
            memberDTO.setNickName(kakaoDTO.getNickName());
            memberDTO.setEmail(kakaoDTO.getEmail());
            memberDTO.setLoginType(kakaoDTO.getLoginType());

            authMapper.insertMember(memberDTO);
            kakaoDTO.setMemberNo(memberDTO.getMemberNo());
            log.info("Member Insert 완료 = {}", kakaoDTO.getMemberNo());

            authMapper.insertSocialMember(kakaoDTO);
            Long socialId = kakaoDTO.getSocialId();
            log.info("SocialMember Insert 완료 = {}", socialId);

            memberDTO = authMapper.findMemberBySocialId(socialId);
            log.info("Member 조회 완료 = {}", memberDTO);
        }
        CustomUserDetails userDetails = new CustomUserDetails(memberDTO);

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        HttpSession session = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getSession(true);

        if (session != null) {
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());
            log.info("Session updated with new authentication details");
        }
    }
}
