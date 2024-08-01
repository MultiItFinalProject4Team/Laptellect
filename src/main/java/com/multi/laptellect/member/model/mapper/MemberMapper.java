package com.multi.laptellect.member.model.mapper;

import com.multi.laptellect.member.model.dto.AddressDTO;
import com.multi.laptellect.member.model.dto.MemberDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface MemberMapper {

    @Select("SELECT * FROM mem_member WHERE member_no = #{ memberNo }")
    MemberDTO findMemberByNo(int memberNo);

    @Select("SELECT member_password FROM mem_password WHERE member_no = #{ memberNo }")
    String findPasswordByMemberNo(int memberNo);

    @Select("SELECT * FROM mem_member WHERE member_name = #{ id }")
    MemberDTO findMemberById(String id);

    @Select("SELECT * FROM mem_member WHERE email = #{ email } AND login_type = 'local'")
    MemberDTO findMemberByEmail(String email);

    @Select("SELECT * FROM mem_member WHERE nick_name = #{ nickName }")
    MemberDTO findMemberByNickName(String nickName);

    @Select("SELECT * FROM mem_member WHERE tel = #{ tel }")
    MemberDTO findMemberByTel(String tel);

    @Update("UPDATE mem_member SET email = #{ email } WHERE member_no = #{ memberNo }")
    int updateEmail(MemberDTO memberDTO);

    @Update("UPDATE mem_member SET nick_name = #{ nickName } WHERE member_no = #{ memberNo }")
    int updateNickName(MemberDTO memberDTO);

    @Update("UPDATE mem_member SET tel = #{ tel } WHERE member_no = #{ memberNo }")
    int updateTel(MemberDTO memberDTO);

    @Update("UPDATE mem_password SET member_password = #{ password } WHERE member_no = #{ memberNo }")
    void updatePassword(int memberNo, String password);

    void insertAddress(AddressDTO addressDTO);
}
