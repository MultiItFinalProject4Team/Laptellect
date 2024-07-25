package com.multi.laptellect.member.service;

import com.multi.laptellect.member.model.dto.MemberDTO;
import com.multi.laptellect.member.model.mapper.MemberMapper;
import com.multi.laptellect.util.RedisUtil;
import com.multi.laptellect.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService{
    private final RedisUtil redisUtil;
    private final MemberMapper memberMapper;

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
}
