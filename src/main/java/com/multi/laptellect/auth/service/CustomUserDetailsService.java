package com.multi.laptellect.auth.service;


import com.multi.laptellect.auth.model.mapper.AuthMapper;
import com.multi.laptellect.member.model.dto.CustomUserDetails;
import com.multi.laptellect.member.model.dto.MemberDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final AuthMapper authMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        MemberDTO userDTO = authMapper.selectMemberByEmail(username);

        if (userDTO != null) {
            return new CustomUserDetails(userDTO);
        }
        return null;
    }
}
