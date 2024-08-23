package com.multi.laptellect.auth.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.multi.laptellect.auth.model.mapper.AuthMapper;
import com.multi.laptellect.common.model.Email;
import com.multi.laptellect.config.api.DataPortalConfig;
import com.multi.laptellect.config.api.KakaoConfig;
import com.multi.laptellect.member.model.dto.MemberDTO;
import com.multi.laptellect.member.model.mapper.MemberMapper;
import com.multi.laptellect.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 인증/인가에 관한 비지니스 로직을 처리하기 위한 클래스
 *
 * @author : 이강석
 * @fileName : AuthServiceImpl.java
 * @since : 2024-07-26
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService{
    private final AuthMapper authMapper;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final MemberMapper memberMapper;
    private final EmailUtil emailUtil;
    private final RedisUtil redisUtil;
    private final SmsUtil smsUtil;
    private final KakaoConfig kakaoConfig;
    private final DataPortalConfig dataPortalConfig;
    private final RestTemplate restTemplate;

    //    private final SecureRandom secureRandom;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createMember(MemberDTO memberDTO) throws SQLException {
        String loginType = memberDTO.getLoginType();

        // 비밀번호 암호화
        String bPw = bCryptPasswordEncoder.encode(memberDTO.getPassword());
        memberDTO.setPassword(bPw);

        if (loginType == null) {
            loginType = "local";
            memberDTO.setLoginType(loginType);

            if (authMapper.insertMember(memberDTO) == 0) throw new RuntimeException("Failed to insert member");

            if (authMapper.insertPassword(memberDTO) == 0) throw new RuntimeException("Failed to insert password");
        } else if(loginType.equals("seller")){
            memberDTO.setRole("ROLE_SELLER");
            log.info("판매자 회원가입 DTO = {}", memberDTO);

            if (authMapper.insertMember(memberDTO) == 0) throw new RuntimeException("Failed to insert member");

            if (authMapper.insertPassword(memberDTO) == 0) throw new RuntimeException("Failed to insert password");

            if (authMapper.insertSeller(memberDTO) == 0) throw new RuntimeException("Failed to insert password");
        }
    }

    @Override
    public void sendVerifyEmail(Email email) throws Exception { // 이메일 인증
        String verifyCode;

        do {
            verifyCode = CodeGenerator.createRandomString(8);
        } while (redisUtil.getData(verifyCode) != null);

        email.setMailTitle("Laptellect 인증번호 요청");
        email.setMailContent("인증번호 : " + verifyCode);

        emailUtil.sendEmail(email);

        redisUtil.setDataExpire(verifyCode, email.getReceiveAddress(), 60*3L);
    }

    @Override
    public boolean isVerifyEmail(String verifyCode) throws Exception { // 인증코드 검증
        String redisVerifyCode = redisUtil.getData(verifyCode);

        // 프론트에서 바꿀 가능성 있으므로 작업 후 인증코드 삭제하는 로직 추가해야함
        // ex) redisUtil.deleteData(verifyCode);
        return redisVerifyCode != null;
    }

    @Override
    public void sendTempPassword(Email email) throws Exception { // 임시 비밀번호 발급 및 이메일 전송 메서드
        String loginType = SecurityUtil.getUserDetails().getLoginType();

        MemberDTO userData = memberMapper.findMemberByEmail(email.getReceiveAddress());
        int memberNo = userData.getMemberNo();

        // 임시 비밀번호 생성
        String tempPasswordStr = CodeGenerator.createRandomString(8);

        email.setMailTitle("Laptellect 임시 비밀번호");
        email.setMailContent("임시 비밀번호 : " + tempPasswordStr);

        emailUtil.sendEmail(email);

        // 회원번호 Key, 임시 비밀번호 Value
        redisUtil.setDataExpire(String.valueOf(memberNo), tempPasswordStr, 60*3L);
    }

    @Override
    public boolean isMemberById(String id) { // id check
        return memberMapper.findMemberById(id) != null;
    }

    @Override
    public boolean isMemberByEmail(String email) { // email check
        return memberMapper.findMemberByEmail(email) != null;
    }

    @Override
    public boolean isMemberByNickName(String nickName) { // NickName check
        return memberMapper.findMemberByNickName(nickName) != null;
    }

    @Override
    public boolean isSocialMember() { // social member check
        String loginType = SecurityUtil.getUserDetails().getLoginType();

        return loginType.equals("kakao") || loginType.equals("naver");
    }

    @Override
    public boolean isMemberByPassword(String password) {
        int memberNo = SecurityUtil.getUserNo();
        String userId = SecurityUtil.getUserDetails().getMemberName();
        String tempPasswordKey = "password:" + userId;
        String tempPassword = redisUtil.getData(tempPasswordKey); // 임시 비밀번호 가져오기

        String userPassword = memberMapper.findPasswordByMemberNo(memberNo);

        return bCryptPasswordEncoder.matches(password, userPassword) || password.equals(tempPassword);
    }

    @Override
    public boolean isPasswordsDifferent(String beforePassword, String afterPassword) {
        return (!beforePassword.equals(afterPassword));
    }

    @Override
    public void sendSms(String tel) throws Exception {
        String verifyCode;
        String text;

        do {
            verifyCode = CodeGenerator.createRandomString(6);
        } while (redisUtil.getData(verifyCode) != null);

        text = "아래의 인증번호를 입력해주세요\n" + verifyCode;

        smsUtil.sendOne(tel, text);

        redisUtil.setDataExpire(verifyCode, String.valueOf(tel), 60*3L);
    }

    @Override
    public boolean isVerifyTel(String verifyCode, String tel) throws Exception {
        String redisTel = redisUtil.getData(verifyCode);

        if (redisTel == null) {
            return false;
        }

        return redisTel.equals(tel);
    }

    @Override
    public int isRegistrationNo(MemberDTO memberDTO) throws Exception {
        String baseUrl = dataPortalConfig.getDataPortalURL();
        String apiKey = dataPortalConfig.getDataPortalBusinessApiKey();

        String url = baseUrl + apiKey;
        URI uri = new URI(url);

        String ownerName = memberDTO.getOwnerName();
        String businessDate = memberDTO.getBusinessDate();
        String registrationNo = memberDTO.getRegistrationNo();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        Map<String, String> businessInfo = new HashMap<>();
        businessInfo.put("b_no", registrationNo);
        businessInfo.put("start_dt", businessDate);
        businessInfo.put("p_nm", ownerName);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("businesses", Collections.singletonList(businessInfo));

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, requestEntity, String.class);

        String responseBody = response.getBody();

        JsonElement jsonElement = JsonParser.parseString(responseBody);
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        JsonArray dataArray = jsonObject.getAsJsonArray("data");

        for (JsonElement element : dataArray) {
            JsonObject dataObject = element.getAsJsonObject();
            String valid = dataObject.get("valid").getAsString();

            if ("01".equals(valid)) {
                log.info("사업자 번호 확인 완료 : {}", valid);
                return 1;
            } else if ("02".equals(valid)) {
                log.error("없는 사업자 번호 : {}", valid);
                return 2;
            }
        }

        return 0;
    }

}
