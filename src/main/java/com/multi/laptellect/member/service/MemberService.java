package com.multi.laptellect.member.service;

import com.multi.laptellect.member.model.dto.MemberDTO;

public interface MemberService {
    boolean updateEmail(MemberDTO memberDTO, String verifyCode) throws Exception;

    boolean updateNickName(MemberDTO memberDTO) throws Exception;

    boolean updateTel(MemberDTO memberDTO, String verifyCode) throws Exception;

    String findUserId(MemberDTO memberDTO) throws Exception;
}
