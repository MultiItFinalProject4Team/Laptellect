package com.multi.laptellect.member.model.mapper;

import com.multi.laptellect.member.model.dto.MemberDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface MemberMapper {

    @Select("SELECT member_no FROM mem_member WHERE member_name = #{ id }")
    MemberDTO findMemberById(String id);

    @Select("SELECT member_no FROM mem_member WHERE email = #{ email }")
    MemberDTO findMemberByEmail(String email);

    @Select("SELECT member_no FROM mem_member WHERE nick_name = #{ nickName }")
    MemberDTO findMemberByNickName(String nickName);
}
