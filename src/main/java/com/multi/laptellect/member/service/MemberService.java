package com.multi.laptellect.member.service;

import com.multi.laptellect.member.model.dto.AddressDTO;
import com.multi.laptellect.member.model.dto.MemberDTO;

/**
 * The interface Member service.
 */
public interface MemberService {
    /**
     * 사용자 이메일 업데이트
     *
     * @param memberDTO  사용자 이메일을 담은 DTO 객체
     * @param verifyCode 인증코드 검증을 위한 파라미터
     * @return the boolean
     * @throws Exception the exception
     */
    boolean updateEmail(MemberDTO memberDTO, String verifyCode) throws Exception;

    /**
     * 사용자 닉네임 업데이트
     *
     * @param memberDTO 사용자 닉네임을 담은 DTO 객체
     * @return the boolean
     * @throws Exception the exception
     */
    boolean updateNickName(MemberDTO memberDTO) throws Exception;

    /**
     * 사용자 휴대폰 번호 업데이트
     *
     * @param memberDTO  사용자 휴대폰 번호가 담긴 DTO 객체
     * @param verifyCode 인증번호 검증을 위한 파라미터
     * @return the boolean
     * @throws Exception the exception
     */
    boolean updateTel(MemberDTO memberDTO, String verifyCode) throws Exception;

    /**
     * 사용자 ID 검증을 위한 메서드
     *
     * @param memberDTO 이메일 또는 전화번호가 담긴 DTO 객체
     * @return the string
     * @throws Exception the exception
     */
    String findUserId(MemberDTO memberDTO) throws Exception;

    /**
     * 사용자 비밀번호 업데이트
     *
     * @param beforePassword 변경 전 비밀번호 파라미터
     * @param afterPassword 변경 할 비밀번호 파라미터
     * @return the boolean
     */
    boolean updatePassword(String beforePassword, String afterPassword);

    int createMemberAddress(AddressDTO addressDTO) throws Exception;
}
