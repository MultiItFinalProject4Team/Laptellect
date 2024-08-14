package com.multi.laptellect.admin.member.model.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

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
}
