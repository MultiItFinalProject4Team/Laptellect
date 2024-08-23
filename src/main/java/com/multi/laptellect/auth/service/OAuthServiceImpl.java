package com.multi.laptellect.auth.service;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.multi.laptellect.auth.model.mapper.AuthMapper;
import com.multi.laptellect.config.api.GoogleConfig;
import com.multi.laptellect.config.api.KakaoConfig;
import com.multi.laptellect.member.model.dto.CustomUserDetails;
import com.multi.laptellect.member.model.dto.MemberDTO;
import com.multi.laptellect.member.model.dto.SocialDTO;
import com.multi.laptellect.util.CodeGenerator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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


/**
 * 소셜 인가/인증 관련 클래스
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class OAuthServiceImpl implements OAuthService{
    private final KakaoConfig kakaoConfig;
    private final GoogleConfig googleConfig;
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
    public SocialDTO getKaKaoProfileInfo(String accessToken) {
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
        String id = jsonObject.get("id").getAsString();
        String email = jsonObject.get("kakao_account").getAsJsonObject().get("email").getAsString();

        log.info("카카오 JSON 리턴 = {}", jsonObject);

        SocialDTO socialDTO = new SocialDTO();
        socialDTO.setExternalId(id);
        socialDTO.setEmail(email);

        log.info("카카오 파싱 = {}", socialDTO);

        return socialDTO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String processKakaoUser(SocialDTO socialDTO) {
        MemberDTO memberDTO = new MemberDTO();
        socialDTO.setLoginType("kakao");

        String externalId = socialDTO.getExternalId();
        SocialDTO kakaoUser = authMapper.findSocialMemberByExternalId(externalId);

        log.debug("카카오 로그인 실행 = {}", externalId);
        if(kakaoUser != null) {
            int memberNo = kakaoUser.getMemberNo();
            memberDTO = authMapper.findMemberByMemberNo(memberNo);
            log.info("존재하는 사용자 = {}", memberDTO);
        } else {
            memberDTO.setMemberName(CodeGenerator.createRandomString(5));
            memberDTO.setNickName(socialDTO.getNickName());
            memberDTO.setEmail(socialDTO.getEmail());
            memberDTO.setLoginType(socialDTO.getLoginType());

            authMapper.insertMember(memberDTO);
            socialDTO.setMemberNo(memberDTO.getMemberNo());
            log.info("Member Insert 완료 = {}", socialDTO.getMemberNo());

            authMapper.insertSocialMember(socialDTO);
            Long socialId = socialDTO.getSocialId();
            log.info("SocialMember Insert 완료 = {}", socialId);

            return "SignUp";
        }
        CustomUserDetails userDetails = new CustomUserDetails(memberDTO);
        log.info("유저 디테일 생성 = {}", userDetails);

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.info("시큐리티 객체 생성 = {}", authentication);

        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = attr.getRequest();
        HttpServletResponse response = attr.getResponse();
        HttpSession session = request.getSession(true);


        if (session != null) {
            new HttpSessionSecurityContextRepository().saveContext(SecurityContextHolder.getContext(), request, response);
            log.info("소셜 로그인 성공.");
        }

        return "SignIn";
    }

    @Override
    public String getGoogleAccessToken(String code) {
        String googleClientId = googleConfig.getGoogleClientId();
        String googleClientSecretKey = googleConfig.getGoogleClientSecretKey();
        String redirectURL = googleConfig.getGoogleRedirectUri();

        String tokenURL = "https://oauth2.googleapis.com/token";

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", googleClientId);
        params.add("client_secret", googleClientSecretKey);
        params.add("redirect_uri", redirectURL);
        params.add("grant_type", "authorization_code");


        HttpEntity<MultiValueMap<String, String>> googleTokenRequest = new HttpEntity<>(params, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                tokenURL, // https://{요청할 서버 주소}
                HttpMethod.POST, // 요청할 방식
                googleTokenRequest, // 요청할 때 보낼 데이터
                String.class // 요청 시 반환되는 데이터 타입
        );

        String responseBody = response.getBody();
        JsonElement element = JsonParser.parseString(responseBody);

        String accessToken = element.getAsJsonObject().get("access_token").getAsString();

        log.info("구글 JSON 반환 = {}", element);
        log.info("구글 엑세스 토큰 = {}", accessToken);

        return accessToken;
    }

    @Override
    public SocialDTO getGoogleProfileInfo(String accessToken) {
        String profileURL = "https://www.googleapis.com/userinfo/v2/me";

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
//        headers.add("Authorization", "Bearer " + accessToken);
        headers.setBearerAuth(accessToken);

        HttpEntity<String> googleTokenRequest = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                profileURL, // https://{요청할 서버 주소}
                HttpMethod.GET, // GET 메소드 사용
                googleTokenRequest, // 요청할 때 보낼 데이터
                String.class // 요청 시 반환되는 데이터 타입
        );

        JsonObject jsonObject = JsonParser.parseString(response.getBody()).getAsJsonObject();
        String id = jsonObject.get("id").getAsString();
        String email = jsonObject.get("email").getAsString();
        String nickName = jsonObject.get("name").getAsString();

        log.info("Google JSON 리턴 = {}", jsonObject);

        SocialDTO SocialDTO = new SocialDTO();

        SocialDTO.setExternalId(id);
        SocialDTO.setEmail(email);
        SocialDTO.setNickName(nickName);

        log.info("Google 파싱 = {}", SocialDTO);

        return SocialDTO;
    }

    @Override
    public String processGoogleUser(SocialDTO socialDTO) {
        MemberDTO memberDTO = new MemberDTO();
        socialDTO.setLoginType("google");

        String externalId = socialDTO.getExternalId();
        SocialDTO googleUser = authMapper.findSocialMemberByExternalId(externalId);

        log.debug("구글 로그인 실행 = {}", externalId);
        if(googleUser != null) {
            int memberNo = googleUser.getMemberNo();
            memberDTO = authMapper.findMemberByMemberNo(memberNo);
            log.info("존재하는 사용자 = {}", memberDTO);
        } else {
            memberDTO.setMemberName(CodeGenerator.createRandomString(5));
            memberDTO.setNickName(socialDTO.getNickName());
            memberDTO.setEmail(socialDTO.getEmail());
            memberDTO.setLoginType(socialDTO.getLoginType());
            log.info("멤버 DTO 확인 = {}", memberDTO);

            authMapper.insertMember(memberDTO);
            socialDTO.setMemberNo(memberDTO.getMemberNo());
            log.info("Member Insert 완료 = {}", socialDTO.getMemberNo());

            authMapper.insertSocialMember(socialDTO);
            Long socialId = socialDTO.getSocialId();
            log.info("SocialMember Insert 완료 = {}", socialId);

            return "SignUp";
        }
        CustomUserDetails userDetails = new CustomUserDetails(memberDTO);

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        HttpSession session = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getSession(true);

        if (session != null) {
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());
            log.info("Session updated with new authentication details");
        }
        return "SignIn";
    }
}
