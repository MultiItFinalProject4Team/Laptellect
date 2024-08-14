package com.multi.laptellect.member.service;

import com.multi.laptellect.common.model.Email;
import com.multi.laptellect.member.model.dto.*;
import com.multi.laptellect.member.model.mapper.MemberMapper;
import com.multi.laptellect.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService{
    private final RedisUtil redisUtil;
    private final MemberMapper memberMapper;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final EmailUtil emailUtil;
    private final SmsUtil smsUtil;
    
    @Override
    @Transactional
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
    @Transactional
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
    @Transactional
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
    public boolean findUserId(MemberDTO memberDTO) throws Exception {
        String memberName = "";
        String email = memberDTO.getEmail();
        String tel = memberDTO.getTel();


        if(email != null) {
            Email emailDTO = new Email();
            memberName = memberMapper.findMemberByEmail(email).getMemberName();

            emailDTO.setReceiveAddress(email);
            emailDTO.setMailTitle("[Laptellect] 아이디 찾기");
            emailDTO.setMailContent("회원님의 아이디는 " + memberName + " 입니다.");

            emailUtil.sendEmail(emailDTO);
            return true;
        } else if(tel != null) {
            memberDTO.setEmail(email);
            memberName = memberMapper.findMemberByTel(tel).getMemberName();
            return true;
        } else {
            throw new Exception();
        }
    }

    @Override
    @Transactional
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
        String username = userInfo.getMemberName();
        String tempPasswordKey = "password:" + username;
        String tempPassword = redisUtil.getData(tempPasswordKey); // 임시 비밀번호 가져오기

        // 변경 전 비밀번호를 다시 검증하여 보안 강화
        if(bCryptPasswordEncoder.matches(beforePassword, userPassword) || beforePassword.equals(tempPassword)) {
            String password = bCryptPasswordEncoder.encode(afterPassword);
            log.info("변경할 비밀번호 = {} {}",memberNo, password);
            memberMapper.updatePassword(memberNo, password);
            log.info("비밀번호 변경 성공 {}", password);
        } else {
            log.info("비밀번호 검증 실패", beforePassword);
            return false;
        }

        return true;
    }

    @Override
    @Transactional
    public int createMemberAddress(AddressDTO addressDTO) throws Exception {
        int memberNo = SecurityUtil.getUserNo();
        addressDTO.setMemberNo(memberNo);

        String addressName = addressDTO.getAddressName();
        int addressCount = memberMapper.findAddressCount(memberNo);

        // 배송지가 10개 초과하지 않도록 검증하기 위함
        if(addressCount >= 10) {
            log.error("사용자 배송지 10개 초과 = {}", addressCount);
            return 101;
        }
        if(memberMapper.insertAddress(addressDTO) != 0) {
            log.info("주소 Insert 완료 = {}", addressDTO);
            return 1;
        } else {
            throw  new Exception("Insert Error");
        }
    }

    @Override
    public ArrayList<AddressDTO> findAllAddressByMemberNo() throws Exception {
        int memberNo = SecurityUtil.getUserNo();

        return memberMapper.findAllAddressByMemberNo(memberNo);
    }

    @Override
    public AddressDTO findAddressByAddressId(int addressId) throws Exception {
        return memberMapper.findAllAddressByAddressId(addressId);
    }

    @Override
    public Page<PointLogDTO> getAllPointList(Pageable pageable) throws Exception{
        int memberNo = SecurityUtil.getUserNo();

        ArrayList<PointLogDTO> pointList = memberMapper.findAllPointLogByMemberNo(memberNo, pageable);
        log.info("pointLIst 조회 = {}", pointList);

        int total = memberMapper.countAllPointLogByMemberNo(memberNo, null);
        log.info("count 조회 = {}", total);

        return new PageImpl<>(pointList, pageable, total);
    }

    @Override
    public Page<PointLogDTO> getAllSavePointList(Pageable pageable) throws Exception {
        int memberNo = SecurityUtil.getUserNo();

        ArrayList<PointLogDTO> pointList = memberMapper.findAllSavePointLogByMemberNo(memberNo, pageable);
        log.info("pointLIst 조회 = {}", pointList);

        int total = memberMapper.countAllPointLogByMemberNo(memberNo, "save");
        log.info("count 조회 = {}", total);

        return new PageImpl<>(pointList, pageable, total);
    }

    @Override
    public Page<PointLogDTO> getAllUsePointList(Pageable pageable) throws Exception {
        int memberNo = SecurityUtil.getUserNo();

        ArrayList<PointLogDTO> pointList = memberMapper.findAllUsePointLogByMemberNo(memberNo, pageable);
        log.info("pointLIst 조회 = {}", pointList);

        int total = memberMapper.countAllPointLogByMemberNo(memberNo, "use");
        log.info("count 조회 = {}", total);

        return new PageImpl<>(pointList, pageable, total);
    }

    @Override
    public int sendTempPassword(String userId, String email) throws Exception{
        Email emailDTO = new Email();
        String memberInfo = null;
        String type = null;
        String tempPassword = CodeGenerator.createRandomString(10);

        // Email로 임시 비밀번호 발급 했을 시 Null이 아니고 공백이 아닐경우 실행
        if(email != null && !email.equals("")) {
            memberInfo = memberMapper.findMemberByEmail(email).getMemberName();

            // 입력한 아이디의 email이 동일하면 이메일 전송 아닐 시 에러를 의미하는 숫자를 반환
            if(userId.equals(memberInfo)) {
                type = email;
                emailDTO.setReceiveAddress(email);
                emailDTO.setMailTitle("[Laptellect] 비밀번호 찾기");
                emailDTO.setMailContent("회원님의 임시 비밀번호는 " + tempPassword + " 입니다.");
                emailUtil.sendEmail(emailDTO);
            } else {
                return 2;
            }
        }

        redisUtil.setDataExpire("password:" + userId, tempPassword, 60L * 3);

        return 1;
    }

    @Override
    public boolean deleteMember() throws Exception {
        CustomUserDetails userDetails = SecurityUtil.getUserDetails();
        int memberNo = userDetails.getMemberNo();
        String loginType = userDetails.getLoginType();
        boolean result;

        log.debug("회원 탈퇴 시작 = {}", memberNo);
        if(loginType.equals("local")) {
            log.info("일반 회원 탈퇴 시작 = {}", loginType);

            result = memberMapper.deleteMember(memberNo) > 0;
            log.info("일반 회원 탈퇴 완료 = {}", result);
        } else {
            log.info("소셜 회원 탈퇴 시작 = {}", loginType);

            SocialDTO socialDTO = memberMapper.findSocialNoByMemberNo(memberNo);
            result = memberMapper.deleteSocialMember(socialDTO.getSocialId()) > 0 &&  memberMapper.deleteMember(memberNo) > 0;
            log.info("소셜 회원 탈퇴 완료 = {}", result);
        }

        return result;
    }

    @Override
    @Transactional
    public int updateMemberAddress(AddressDTO addressDTO) {
        int userNo = SecurityUtil.getUserNo();
        int memberNo = addressDTO.getMemberNo();

        log.debug("유저 일치 검증 시작 = {} {}", userNo, memberNo);
        // 로그인 한 유저가 지우는 배송지의 유저와 일치하지 않으면 삭제 거부
        if(userNo != memberNo) return 401;

        log.debug("배송지 주소 업데이트 시작 = {}", addressDTO);
        return memberMapper.updateAddress(addressDTO);
    }

    @Override
    @Transactional
    public boolean deleteMemberAddress(int addressId) {
        int userNo = SecurityUtil.getUserNo();
        int memberNo = memberMapper.findOwnerByAddressId(addressId);
        
        log.debug("사용자 검증 시작 = {} {}", userNo, memberNo);
        // 로그인 한 유저가 지우는 배송지의 유저와 일치하지 않으면 삭제 거부
        if(userNo != memberNo) return false;
        
        log.debug("배송지 삭제 시작 = {}", addressId);
        return memberMapper.deleteAddressByAddressId(addressId) != 0;
    }

    @Override
    @Transactional
    public int updatePoint(MemberDTO memberDTO) throws Exception {
        int result = memberMapper.updatePoint(memberDTO);
        if (result == 0) {
            throw new RuntimeException("포인트 업데이트 실패");
        }
        log.info("포인트 업데이트 완료 = {}", memberDTO.getPoint());

        // 업데이트된 MemberDTO로 UserDetails 갱신
        memberDTO = memberMapper.findMemberByNo(memberDTO.getMemberNo());
        SecurityUtil.updateUserDetails(memberDTO);

        return result;
    }
}
