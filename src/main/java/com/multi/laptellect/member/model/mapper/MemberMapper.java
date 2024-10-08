package com.multi.laptellect.member.model.mapper;

import com.multi.laptellect.admin.model.dto.LoginLog;
import com.multi.laptellect.member.model.dto.AddressDTO;
import com.multi.laptellect.member.model.dto.MemberDTO;
import com.multi.laptellect.member.model.dto.PointLogDTO;
import com.multi.laptellect.member.model.dto.SocialDTO;
import org.apache.ibatis.annotations.*;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;

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

    @Select("SELECT * FROM mem_member WHERE email = #{ email } AND login_type = #{ loginType }")
    MemberDTO findMemberBySocialEmail(SocialDTO socialDTO);

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
    void updatePassword(@Param("memberNo") int memberNo, @Param("password") String password);

    int insertAddress(AddressDTO addressDTO);

    ArrayList<AddressDTO> findAllAddressByMemberNo(int memberNo);

    @Select("SELECT COUNT(*) FROM mem_delivery_address WHERE member_no = #{ memberNo }")
    int findAddressCount(int memberNo);

    @Select("SELECT * FROM mem_delivery_address WHERE address_id = #{ addressId }")
    AddressDTO findAllAddressByAddressId(int addressId);

    @Select("SELECT member_no FROM mem_delivery_address WHERE address_id = #{ addressId }")
    int findOwnerByAddressId(int addressId);

    @Delete("DELETE FROM mem_delivery_address WHERE address_id = #{ addressId }")
    int deleteAddressByAddressId(int addressId);

    int updateAddress(AddressDTO addressDTO);


    int updatePoint(MemberDTO memberDTO);

    @Select("SELECT * FROM payment_point WHERE member_no = #{ memberNo } ORDER BY payment_point_no DESC LIMIT #{ pageable.pageSize } OFFSET #{ pageable.offset }")
    ArrayList<PointLogDTO> findAllPointLogByMemberNo(@Param("memberNo") int memberNo, @Param("pageable") Pageable pageable);


    int countAllPointLogByMemberNo(@Param("memberNo") int memberNo, @Param("type") String type);

    @Select("SELECT * FROM payment_point WHERE member_no = #{ memberNo } AND payment_point_change > 0 ORDER BY payment_point_no DESC LIMIT #{ pageable.pageSize } OFFSET #{ pageable.offset }")
    ArrayList<PointLogDTO> findAllSavePointLogByMemberNo(@Param("memberNo") int memberNo, @Param("pageable") Pageable pageable);
    @Select("SELECT * FROM payment_point WHERE member_no = #{ memberNo } AND payment_point_change < 0 ORDER BY payment_point_no DESC LIMIT #{ pageable.pageSize } OFFSET #{ pageable.offset }")
    ArrayList<PointLogDTO> findAllUsePointLogByMemberNo(@Param("memberNo") int memberNo, @Param("pageable") Pageable pageable);

    @Select("SELECT * FROM mem_social_member WHERE member_no = #{ memberNo }")
    SocialDTO findSocialNoByMemberNo(int memberNo);

    @Delete("DELETE FROM mem_social_member WHERE social_id = #{ socialId }")
    int deleteSocialMember(Long socialId);

    @Update("UPDATE mem_member SET nick_name = null, email = null, is_active = 'Y', is_active_at = now() WHERE member_no = #{ memberNo }")
    int deleteMember(int memberNo);
}
