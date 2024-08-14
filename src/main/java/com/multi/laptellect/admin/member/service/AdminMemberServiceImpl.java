package com.multi.laptellect.admin.member.service;

import com.multi.laptellect.admin.member.model.mapper.AdminMemberMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
}
