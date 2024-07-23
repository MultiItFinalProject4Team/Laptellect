package com.multi.laptellect.auth.service;

import com.multi.laptellect.auth.model.mapper.AuthMapper;
import com.multi.laptellect.common.model.Email;
import com.multi.laptellect.member.model.dto.MemberDTO;
import com.multi.laptellect.member.model.mapper.MemberMapper;
import com.multi.laptellect.util.CodeGenerator;
import com.multi.laptellect.util.EmailUtil;
import com.multi.laptellect.util.RedisUtil;
import com.multi.laptellect.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService{
    private final AuthMapper authMapper;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final MemberMapper memberMapper;
    private final EmailUtil emailUtil;
    private final RedisUtil redisUtil;

    //    private final SecureRandom secureRandom;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createMember(MemberDTO memberDTO) throws SQLException {

        // 비밀번호 암호화
        String bPw = bCryptPasswordEncoder.encode(memberDTO.getPassword());
        memberDTO.setPassword(bPw);

        if(memberDTO.getLoginType().equals("local")) { // 일반 회원가입
            if(authMapper.insertMember(memberDTO) == 0) {
                throw new SQLException("Failed to insert member");
            }

            if(authMapper.insertPassword(memberDTO) == 0) {
                throw new SQLException("Failed to insert password");
            }
        } else { // 소셜 회원가입
            // ID 설정 ( 추후 수정 )
            if(authMapper.insertMember(memberDTO) == 0) {
                throw new SQLException("Failed to insert member");
            }
        }

        switch (memberDTO.getLoginType()) {
            case "kakao":
            case "naver":
                // 소셜 회원 가입
                if (authMapper.insertMember(memberDTO) == 0) {
                    throw new SQLException("Failed to insert member");
                }
                break;

//            case "seller":
//                // 판매자 회원 가입
//                if (authMapper.insertMember(memberDTO) == 0) {
//                    throw new SQLException("Failed to insert member");
//                }
//                if (authMapper.insertPassword(memberDTO) == 0) {
//                    throw new SQLException("Failed to insert password");
//                }
//
//                if (authMapper.insertSeller(memberDTO))
//                break;

            default:
                // 일반 회원 가입
                if (authMapper.insertMember(memberDTO) == 0) {
                    throw new SQLException("Failed to insert member");
                }
                if (authMapper.insertPassword(memberDTO) == 0) {
                    throw new SQLException("Failed to insert password");
                }
                break;
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

        return redisVerifyCode != null;
    }

    @Override
    public void sendTempPassword(Email email) throws Exception { // 임시 비밀번호 발급 및 이메일 전송 메서드
        // 이메일 존재 검증
        MemberDTO userData = memberMapper.findMemberByEmail(email.getReceiveAddress());

        int memberNo = userData.getMemberNo();

        // 임시 비밀번호 생성
        String tempPasswordStr = CodeGenerator.createRandomString(8);

        email.setMailTitle("Laptellect 임시 비밀번호");
        email.setMailContent("임시 비밀번호 : " + tempPasswordStr);

        emailUtil.sendEmail(email);

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

}
