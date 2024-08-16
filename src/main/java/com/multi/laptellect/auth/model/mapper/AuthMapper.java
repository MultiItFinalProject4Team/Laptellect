package com.multi.laptellect.auth.model.mapper;

import com.multi.laptellect.member.model.dto.SocialDTO;
import com.multi.laptellect.member.model.dto.MemberDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AuthMapper {
    MemberDTO selectMemberById(String memberName);

    int insertMember(MemberDTO memberDTO);

    int insertPassword(MemberDTO memberDTO);

    int insertSocialMember(SocialDTO socialDTO);

//    MemberDTO findMemberByExternalId(Long externalId);

    SocialDTO findSocialMemberByExternalId(String externalId);

    MemberDTO findMemberBySocialId(Long socialId);

    MemberDTO findMemberByMemberNo(int memberNo);

    int insertSeller(MemberDTO memberDTO);
}
