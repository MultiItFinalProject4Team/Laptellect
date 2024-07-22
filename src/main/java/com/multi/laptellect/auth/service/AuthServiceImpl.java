package com.multi.laptellect.auth.service;

import com.multi.laptellect.auth.model.mapper.AuthMapper;
import com.multi.laptellect.member.model.dto.MemberDTO;
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

    //    private final SecureRandom secureRandom;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createMember(MemberDTO memberDTO) throws SQLException {
        // 소셜 회원가입 시 ID 설정 ( 추후 수정 )
//        memberDTO.setUserName(memberDTO.getLoginType() != null ? createUserName() : memberDTO.getUserName());

        // 비밀번호 암호화
        String bPw = bCryptPasswordEncoder.encode(memberDTO.getPassword());
        memberDTO.setPassword(bPw);

        if(authMapper.insertMember(memberDTO) == 0) {
            throw new SQLException("Failed to insert member");
        }

        if(authMapper.insertPassword(memberDTO) == 0) {
            throw new SQLException("Failed to insert password");
        }
    }

}
