package com.multi.laptellect.admin.member.service;

import com.multi.laptellect.admin.member.model.dto.AdminMemberDTO;
import com.multi.laptellect.admin.member.model.mapper.AdminMemberMapper;
import com.multi.laptellect.admin.model.dto.LoginLog;
import com.multi.laptellect.common.model.PagebleDTO;
import com.multi.laptellect.common.model.mapper.LogMapper;
import com.multi.laptellect.member.model.dto.MemberDTO;
import com.multi.laptellect.member.model.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * Please explain the class!!
 *
 * @author : 이강석
 * @fileName : AdminMemberServiceImpl
 * @since : 2024-08-13
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminMemberServiceImpl implements AdminMemberService{
    private final AdminMemberMapper adminMemberMapper;
    private final MemberMapper memberMapper;
    private final LogMapper logMapper;

    @Override
    public int findNewMemberCount() throws Exception {
        return adminMemberMapper.findNewMemberCount();
    }

    @Override
    public int findMemberCount() throws Exception {
        return adminMemberMapper.findMemberCount();
    }

    @Override
    public Page<AdminMemberDTO> getMemberList(PagebleDTO pagebleDTO) throws Exception {
        log.info("파라미터 확인 = {}", pagebleDTO);

        ArrayList<AdminMemberDTO> members = adminMemberMapper.findAllMember(pagebleDTO);
        int total = adminMemberMapper.countAllMember(pagebleDTO);
        log.info("회원 리스트 조회 완료 = {}", members);

        return new PageImpl<>(members, pagebleDTO, total);
    }

    @Override
    public int deleteMember(int memberNo) throws Exception {
        MemberDTO memberDTO = memberMapper.findMemberByNo(memberNo);
        String loginType = memberDTO.getLoginType();

        switch (loginType) {
            case "local" -> {
                adminMemberMapper.deleteMember(memberNo);
                log.info("일반 회원 삭제 성공 = {}", loginType);
                return 1;
            }

            case "kakao", "google" -> {
                adminMemberMapper.deleteSocialMember(memberNo);
                adminMemberMapper.deleteMember(memberNo);
                log.info("소셜 회원 삭제 성공 = {}", loginType);
                return 2;
            }

            default -> {
                adminMemberMapper.deleteSellerMember(memberNo);
                adminMemberMapper.deleteMember(memberNo);
                log.info("판매자 회원 삭제 성공 = {}", loginType);
                return 3;
            }
        }
    }

    @Override
    public MemberDTO findMemberByMemberNo(int memberNo) throws Exception {
        MemberDTO member = memberMapper.findMemberByNo(memberNo);
        log.info("회원 조회 완료 = {}", member);
        return member;
    }

    @Override
    public LoginLog findLoginLogByMemberNo(int memberNo) throws Exception {
        LoginLog loginLog = logMapper.findLoginLogByMemberNo(memberNo);
        log.info("로그인 로그 조회 완료 = {}", loginLog);
        return loginLog;
    }
}
