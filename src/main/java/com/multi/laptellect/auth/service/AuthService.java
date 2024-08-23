package com.multi.laptellect.auth.service;

import com.multi.laptellect.common.model.Email;
import com.multi.laptellect.member.model.dto.MemberDTO;

import java.sql.SQLException;

/**
 * 인증/인가 Service 클래스
 *
 * @author : 이강석
 * @fileName : AuthService.java
 * @since : 2024-07-30
 */
public interface AuthService {
    /**
     * 회원 등록
     *
     * @param memberDTO 회원 정보가 담긴 DTO
     * @throws SQLException the sql exception
     */
    void createMember(MemberDTO memberDTO) throws SQLException;

    /**
     * Email 인증 번호 송신
     *
     * @param email 발신 이메일 정보가 담긴 DTO
     * @throws Exception the exception
     */
    void sendVerifyEmail(Email email) throws Exception;

    /**
     * Email 인증 번호 검증
     *
     * @param verifyCode 인증 번호
     * @return boll값으로 반환
     * @throws Exception the exception
     */
    boolean isVerifyEmail(String verifyCode) throws Exception;

    /**
     * 임시 비밀번호 Email 전송
     *
     * @param email 발신 이메일 정보가 담긴 DTO
     * @throws Exception the exception
     */
    void sendTempPassword(Email email) throws Exception;

    /**
     * 아이디 중복 검증
     *
     * @param id 입력한 ID값
     * @return the boolean
     */
    boolean isMemberById(String id);

    /**
     * 소셜 멤버 검증
     *
     * @return the boolean
     */
    boolean isSocialMember();

    /**
     * 이메일 중복 검증.
     *
     * @param email 사용자가 입력한 이메일이 담긴 DTO
     * @return the boolean
     */
    boolean isMemberByEmail(String email);

    /**
     * 닉네임 중복 검증
     *
     * @param nickName 사용자가 입력한 닉네임
     * @return the boolean
     */
    boolean isMemberByNickName(String nickName);

    /**
     * 비밀번호 변경 시 현재 비밀번호 검증
     *
     * @param password 사용자가 입력한 현재 비밀번호
     * @return the boolean
     */
    boolean isMemberByPassword(String password);

    /**
     * 현재 비밀번호와 변경할 비밀번호 검증
     *
     * @param beforePassword 이전 비밀번호
     * @param afterPassword  변경할 비밀번호
     * @return the boolean
     */
    boolean isPasswordsDifferent(String beforePassword, String afterPassword);

    /**
     * SMS 인증 보내기
     *
     * @param tel 사용자가 입력한 휴대폰 번호
     * @throws Exception the exception
     */
    void sendSms(String tel) throws Exception;

    /**
     * SMS 인증 번호 검증
     *
     * @param verifyCode 사용자가 입력한 인증 번호
     * @param tel
     * @return the boolean
     * @throws Exception the exception
     */
    boolean isVerifyTel(String verifyCode, String tel) throws Exception;

    int isRegistrationNo(MemberDTO memberDTO) throws Exception;
}
