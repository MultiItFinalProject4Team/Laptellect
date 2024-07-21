package com.multi.laptellect.config.Security;


import com.multi.laptellect.auth.model.mapper.AuthMapper;
import com.multi.laptellect.member.model.dto.CustomUserDetails;
import com.multi.laptellect.member.model.dto.MemberDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {
    private final AuthMapper authMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        MemberDTO userDTO = authMapper.selectMemberById(username);

        if (userDTO == null) {
            log.error("log error = {}", username );
            throw  new UsernameNotFoundException("존재하지 않는 ID :" + username);
        }
        return new CustomUserDetails(userDTO);
    }
}
