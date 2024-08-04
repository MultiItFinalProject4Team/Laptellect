package com.multi.laptellect.member.service;

import com.multi.laptellect.member.model.dto.AddressDTO;
import com.multi.laptellect.member.model.dto.CustomUserDetails;
import com.multi.laptellect.member.model.dto.MemberDTO;
import com.multi.laptellect.member.model.dto.PointLogDTO;
import com.multi.laptellect.member.model.mapper.MemberMapper;
import com.multi.laptellect.util.RedisUtil;
import com.multi.laptellect.util.SecurityUtil;
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
}
