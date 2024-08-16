package com.multi.laptellect.admin.member.service;

import com.multi.laptellect.admin.member.model.dto.AdminMemberDTO;
import com.multi.laptellect.admin.member.model.mapper.AdminMemberMapper;
import com.multi.laptellect.common.model.PagebleDTO;
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

}
