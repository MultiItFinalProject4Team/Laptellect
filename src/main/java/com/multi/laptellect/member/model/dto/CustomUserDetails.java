package com.multi.laptellect.member.model.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails, Serializable {
    private static final long serialVersionUID = 1L;
    private final MemberDTO memberDTO;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(memberDTO::getRole);
        return authorities;
    }

    @Override
    public String getPassword() {
        return memberDTO.getPassword();
    }

    @Override
    public String getUsername() {
        return memberDTO.getMemberName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public int getMemberNo() {
        return memberDTO.getMemberNo();
    }

    public String getNickName() {
        return memberDTO.getNickName();
    }

    public String getEmail() {
        return  memberDTO.getEmail();
    }

    public String getTel() {
        return memberDTO.getTel();
    }

    public LocalDateTime getCreateDate() {
        return memberDTO.getCreateDate().toLocalDateTime();
    }

    public int getPoint() {
        return  memberDTO.getPoint();
    }

    public String getLoginType() {
        return  memberDTO.getLoginType();
    }

    public String getMemberName() {
        return memberDTO.getMemberName();
    }

    public void update(MemberDTO updateDTO) {
        this.memberDTO.setMemberName(updateDTO.getMemberName());
        this.memberDTO.setNickName(updateDTO.getNickName());
        this.memberDTO.setEmail(updateDTO.getEmail());
        this.memberDTO.setTel(updateDTO.getTel());
        this.memberDTO.setPoint(updateDTO.getPoint());
    }
}