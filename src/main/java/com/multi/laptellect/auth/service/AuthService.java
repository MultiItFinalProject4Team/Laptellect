package com.multi.laptellect.auth.service;

import com.multi.laptellect.common.model.Email;
import com.multi.laptellect.member.model.dto.MemberDTO;

import java.sql.SQLException;

public interface AuthService {
    void createMember(MemberDTO memberDTO) throws SQLException;

    String createTempPassword();

    void sendTempPassword(Email email) throws Exception;

    boolean isMemberById(String id);

    boolean isMemberByEmail(String email);

    boolean isMemberByNickName(String nickName);

    boolean isSocialMember();
}
