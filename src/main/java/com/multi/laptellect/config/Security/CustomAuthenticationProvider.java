package com.multi.laptellect.config.Security;


import com.multi.laptellect.member.model.dto.CustomUserDetails;
import com.multi.laptellect.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.Serializable;

@Slf4j
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider, Serializable {
    private transient final CustomUserDetailsService customUserDetailsService;
    private transient final PasswordEncoder passwordEncoder;
    private static final long serialVersionUID = 1L;
    private final RedisUtil redisUtil;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // 입력한 ID와 비밀번호 임시 비밀번호 변수에 담기
        String username = authentication.getName(); // 입력한 아이디
        String password = (String)authentication.getCredentials(); // 입력한 비밀번호

        String tempPasswordKey = "password:" + username;
        String tempPassword = redisUtil.getData(tempPasswordKey); // 임시 비밀번호 가져오기

        // CustomUserDetailsService로 변환
        CustomUserDetails customUserDetails = (CustomUserDetails) customUserDetailsService.loadUserByUsername(username);

        // loadUserByUsername에서 username(email)이 DB에 없어서 반환 못한 경우 예외 처리
        if (customUserDetails == null) {
            throw new UsernameNotFoundException("User not found");
        }

        // 비밀번호 or 임시 비밀번호 둘 중 하나가 맞으면 true처리
        boolean passwordMatches = passwordEncoder.matches(password, customUserDetails.getPassword()) ||
                password.equals(tempPassword); // 임시 비밀번호 일치

        // 비밀번호 false 일 시 예외 처리
        if (!passwordMatches) {
            throw new BadCredentialsException("Wrong password") {};
        }

        // 인증 정보가 담긴 객체 반환
        return new UsernamePasswordAuthenticationToken(customUserDetails, password, customUserDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}