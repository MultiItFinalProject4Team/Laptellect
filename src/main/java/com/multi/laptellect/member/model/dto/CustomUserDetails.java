package com.multi.laptellect.member.model.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {
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

    public String getTempPassword() {
        return memberDTO.getTempPassword();
    }

    public boolean getTempPasswordIsUse() {
        return memberDTO.getTempPasswordIsUse();
    }

    public LocalDateTime getTempExpDate() {
        return memberDTO.getTempExpDate().toLocalDateTime();
    }

    @Override
    public String getUsername() {
        return memberDTO.getUserName();
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
        return memberDTO.getUserName();
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

    public void update(MemberDTO updateDTO) {
        this.memberDTO.setUserName(updateDTO.getUserName());
        this.memberDTO.setTel(updateDTO.getTel());
        this.memberDTO.setPoint(updateDTO.getPoint());
    }
}