package com.multi.laptellect.member.service;

import com.multi.laptellect.member.model.dto.AddressDTO;
import com.multi.laptellect.member.model.dto.CustomUserDetails;
import com.multi.laptellect.member.model.dto.MemberDTO;
import com.multi.laptellect.member.model.mapper.MemberMapper;
import com.multi.laptellect.util.RedisUtil;
import com.multi.laptellect.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService{
    private final RedisUtil redisUtil;
    private final MemberMapper memberMapper;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public boolean updateEmail(MemberDTO memberDTO, String verifyCode) throws Exception{ // email update
        if(redisUtil.getData(verifyCode).equals(memberDTO.getEmail())) {
            if(memberMapper.updateEmail(memberDTO) == 0) {
                throw new RuntimeException("이메일 업데이트 실패");
            }
            log.info("이메일 업데이트 완료 = {} ", memberDTO.getEmail());

            // Redis 인증 코드 삭제
            redisUtil.deleteData(verifyCode);

            memberDTO = memberMapper.findMemberByNo(memberDTO.getMemberNo());
            SecurityUtil.updateUserDetails(memberDTO);
            return true;
        } else {
            log.error("updateEmail Error : Email 불일치");
            return false;
        }
    }

    @Override
    public boolean updateNickName(MemberDTO memberDTO) throws Exception{
        if(memberMapper.updateNickName(memberDTO) > 0) {
            log.info("닉네임 업데이트 완료 = {} ", memberDTO.getEmail());

            memberDTO = memberMapper.findMemberByNo(memberDTO.getMemberNo());
            SecurityUtil.updateUserDetails(memberDTO);
            return true;
        } else {
            throw new RuntimeException("닉네임 업데이트 실패");
        }
    }

    @Override
    public boolean updateTel(MemberDTO memberDTO, String verifyCode) throws Exception{
        if(redisUtil.getData(verifyCode).equals(String.valueOf(memberDTO.getMemberNo()))) {
            if(memberMapper.updateTel(memberDTO) == 0) {
                throw new RuntimeException("휴대폰 번호 업데이트 실패");
            }
            log.info("휴대폰 번호 업데이트 완료 = {} ", memberDTO.getEmail());

            // Redis 인증 코드 삭제
            redisUtil.deleteData(verifyCode);

            memberDTO = memberMapper.findMemberByNo(memberDTO.getMemberNo());
            SecurityUtil.updateUserDetails(memberDTO);
            return true;
        } else {
            log.error("updateTel Error : 업데이트 실패");
            return false;
        }
    }

    @Override
    public String findUserId(MemberDTO memberDTO) throws Exception {
        String request = "";
        String memberName = "";
        String email = memberDTO.getEmail();
        String tel = memberDTO.getTel();


        if(email == null) {
            memberDTO.setTel(tel);
            memberName = memberMapper.findMemberByEmail(email).getMemberName();
            request = "회원님의 아이디는 [" + memberName + "] 입니다.";
        } else {
            memberDTO.setEmail(email);
            memberName = memberMapper.findMemberByTel(tel).getMemberName();
            request = "회원님의 아이디는 [" + memberName + "] 입니다.";
        }

        return request;
    }

    @Override
    public boolean updatePassword(String beforePassword, String afterPassword) {
        CustomUserDetails userInfo = SecurityUtil.getUserDetails();
        int memberNo = userInfo.getMemberNo();
        String loginType = userInfo.getLoginType();

        // 현재 비밀번호가 바꿀 비밀번호가 동일할 시 업데이트 거부
        if(beforePassword.equals(afterPassword)) return false;
        
        // local 로그인 유형이 아니면 비밀번호 업데이트 거부 (소셜 회원의 경우)
        if(!loginType.equals("local")) return false;

        String userPassword = memberMapper.findPasswordByMemberNo(memberNo);

        log.debug("비밀번호 업데이트 시작 = {}", beforePassword);
        // 변경 전 비밀번호를 다시 검증하여 보안 강화
        if(bCryptPasswordEncoder.matches(beforePassword, userPassword)) {
            String password = bCryptPasswordEncoder.encode(afterPassword);
            memberMapper.updatePassword(memberNo, password);
            log.info("비밀번호 변경 성공 {}", password);
        } else {
            log.info("비밀번호 검증 실패", beforePassword);
            return false;
        }

        return true;
    }

    @Override
    public int createMemberAddress(AddressDTO addressDTO) throws Exception {
        int result = 1;
        int memberNo = SecurityUtil.getUserDetails().getMemberNo();
        addressDTO.setMemberNo(memberNo);

        String addressName = addressDTO.getAddressName();

        // 배송지 명이 Null이면 Error 발생하므로 검증하기 위함
        if(addressName == null) result = 0;

        memberMapper.insertAddress(addressDTO);
        log.info("주소 Insert 완료 = {}", addressDTO);

        return result;
    }
}
