package com.multi.laptellect.auth.service;

import com.multi.laptellect.common.model.Email;
import com.multi.laptellect.member.model.dto.MemberDTO;

import java.sql.SQLException;

public interface AuthService {
    void createMember(MemberDTO memberDTO) throws SQLException;

    void sendVerifyEmail(Email email) throws Exception;

    boolean isVerifyEmail(String verifyCode) throws Exception;

    void sendTempPassword(Email email) throws Exception;

    boolean isMemberById(String id);

    boolean isMemberByEmail(String email);

    boolean isMemberByNickName(String nickName);

    boolean isSocialMember();

    boolean isMemberByPassword(String password);

    void sendSms(String tel) throws Exception;

    boolean isVerifyTel(String verifyCode) throws Exception;
}
