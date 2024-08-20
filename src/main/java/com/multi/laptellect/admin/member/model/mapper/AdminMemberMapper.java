package com.multi.laptellect.admin.member.model.mapper;

import com.multi.laptellect.admin.member.model.dto.AdminMemberDTO;
import com.multi.laptellect.common.model.PagebleDTO;
import com.multi.laptellect.member.model.dto.MemberDTO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
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

    @Delete("DELETE FROM mem_social_member WHERE member_no = #{ memberNo }")
    void deleteSocialMember(int memberNo);

    @Delete("DELETE FROM mem_seller_member WHERE member_no = #{ memberNo }")
    void deleteSellerMember(int memberNo);

    int updateMember(@Param("memberDTO") MemberDTO memberDTO, @Param("type") String type);

    int updateMemberPassword(MemberDTO memberDTO);
}
