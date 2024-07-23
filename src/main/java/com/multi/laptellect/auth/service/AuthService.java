package com.multi.laptellect.auth.service;

import com.multi.laptellect.member.model.dto.MemberDTO;

import java.sql.SQLException;

public interface AuthService {
    void createMember(MemberDTO memberDTO) throws SQLException;

    boolean isMemberById(String id);

    boolean isMemberByEmail(String email);
}
