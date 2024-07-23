package com.multi.laptellect.auth.service;

import com.multi.laptellect.auth.model.mapper.AuthMapper;
import com.multi.laptellect.member.model.dto.MemberDTO;
import com.multi.laptellect.member.model.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
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
    private final AuthenticationManager authenticationManager;
    private final MemberMapper memberMapper;

    //    private final SecureRandom secureRandom;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createMember(MemberDTO memberDTO) throws SQLException {




        // 비밀번호 암호화
        String bPw = bCryptPasswordEncoder.encode(memberDTO.getPassword());
        memberDTO.setPassword(bPw);

        if(memberDTO.getLoginType().equals("kakao") || memberDTO.getLoginType().equals("naver")) { // 소셜 회원 가입
            // ID 설정 ( 추후 수정 )
//        memberDTO.setUserName(memberDTO.getLoginType() != null ? createUserName() : memberDTO.getUserName());

        } else { // 일반 회원 가입
            if(authMapper.insertMember(memberDTO) == 0) {
                throw new SQLException("Failed to insert member");
            }

            if(authMapper.insertPassword(memberDTO) == 0) {
                throw new SQLException("Failed to insert password");
            }
        }
    }

    @Override
    public boolean isMemberById(String id) { // id check
        return memberMapper.findMemberById(id) != null;
    }

    @Override
    public boolean isMemberByEmail(String email) { // email check
        return memberMapper.findMemberByEmail(email) != null;
    }

}
