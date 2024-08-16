package com.multi.laptellect.admin.member.service;

import com.multi.laptellect.admin.member.model.dto.AdminMemberDTO;
import com.multi.laptellect.common.model.PagebleDTO;
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
}
