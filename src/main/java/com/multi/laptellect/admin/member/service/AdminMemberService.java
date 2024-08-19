package com.multi.laptellect.admin.member.service;

import com.multi.laptellect.admin.member.model.dto.AdminMemberDTO;
import com.multi.laptellect.admin.model.dto.LoginLog;
import com.multi.laptellect.common.model.PagebleDTO;
import com.multi.laptellect.member.model.dto.MemberDTO;
import org.springframework.data.domain.Page;

/**
 * Please explain the class!!
 *
 * @author : 이강석
 * @fileName : AdminMemberService
 * @since : 2024-08-13
 */
public interface AdminMemberService {
    int findNewMemberCount() throws Exception;
    int findMemberCount() throws Exception;

    Page<AdminMemberDTO> getMemberList(PagebleDTO pagebleDTO) throws Exception;

    int deleteMember(int memberNo) throws Exception;

    MemberDTO findMemberByMemberNo(int memberNo) throws Exception;

    LoginLog findLoginLogByMemberNo(int memberNo) throws Exception;
}
