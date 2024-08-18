package com.multi.laptellect.admin.member.model.mapper;

import com.multi.laptellect.admin.member.model.dto.AdminMemberDTO;
import com.multi.laptellect.common.model.PagebleDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.ArrayList;

/**
 * Please explain the class!!
 *
 * @author : 이강석
 * @fileName : AdminMemberMapper
 * @since : 2024-08-13
 */
@Mapper
public interface AdminMemberMapper {
    @Select("SELECT COUNT(*) FROM mem_member WHERE is_active = 'N' AND DATE(created_at) = CURDATE()")
    int findNewMemberCount();

    @Select("SELECT COUNT(*) FROM mem_member WHERE is_active = 'N'")
    int findMemberCount();

    ArrayList<AdminMemberDTO> findAllMember(PagebleDTO pagebleDTO);

    int countAllMember(PagebleDTO pagebleDTO);

    void deleteMember(int memberNo);

    void deleteSocialMember(int memberNo);

    void deleteSellerMember(int memberNo);
}
