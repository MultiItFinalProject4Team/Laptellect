package com.multi.laptellect.member.service;

import com.multi.laptellect.member.model.dto.AddressDTO;
import com.multi.laptellect.member.model.dto.MemberDTO;
import com.multi.laptellect.member.model.dto.PointLogDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;

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
     * @param afterPassword  변경 할 비밀번호 파라미터
     * @return the boolean
     */
    boolean updatePassword(String beforePassword, String afterPassword);

    /**
     * 회원 배송지 정보 생성
     *
     * @param addressDTO 사용자가 입력한 배송지 정보가 담긴 DTO 객체
     * @return the int
     * @throws Exception the exception
     */
    int createMemberAddress(AddressDTO addressDTO) throws Exception;

    /**
     * 회원 배송지 정보 조회
     *
     * @return the array list
     * @throws Exception the exception
     */
    ArrayList<AddressDTO> findAllAddressByMemberNo() throws Exception;

    /**
     * 배송지 업데이트
     *
     * @param addressDTO 업데이트 정보가 담긴 DTO 객체
     * @return the int
     */
    int updateMemberAddress(AddressDTO addressDTO);

    /**
     * 배송지 삭제
     *
     * @param addressId 삭제할 배송지의 PK키
     * @return the boolean
     * @throws Exception the exception
     */
    boolean deleteMemberAddress(int addressId) throws Exception;

    /**
     * 배송지 단일 조회
     *
     * @param addressId 조회할 배송지의 PK키
     * @return DTO 객체 반환
     * @throws Exception the exception
     */
    AddressDTO findAddressByAddressId(int addressId) throws Exception;

    /**
     * 회원 전체 포인트 내역 조회
     *
     * @param pageable 페이징 객체
     * @return Point List 객체
     * @throws Exception the exception
     */
    Page<PointLogDTO> getAllPointList(Pageable pageable) throws Exception;

    /**
     * 회원 적립 포인트 내역 조회
     *
     * @param pageable 페이징 객체
     * @return Point List 객체
     * @throws Exception the exception
     */
    Page<PointLogDTO> getAllSavePointList(Pageable pageable) throws Exception;

    /**
     * 회원 사용 포인트 내역 조회
     *
     * @param pageable 페이징 객체
     * @return Point List 객체
     * @throws Exception the exception
     */
    Page<PointLogDTO> getAllUsePointList(Pageable pageable) throws Exception;
}
