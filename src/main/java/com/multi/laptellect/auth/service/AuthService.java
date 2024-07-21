package com.multi.laptellect.auth.service;

import com.multi.laptellect.auth.model.dto.TokenDTO;
import com.multi.laptellect.member.model.dto.MemberDTO;

import java.sql.SQLException;

public interface AuthService {
    void createMember(MemberDTO memberDTO) throws SQLException;

    TokenDTO login(MemberDTO memberDTO);
}
