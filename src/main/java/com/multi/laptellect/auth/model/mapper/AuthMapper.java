package com.multi.laptellect.auth.model.mapper;

import com.multi.laptellect.member.model.dto.MemberDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AuthMapper {
    MemberDTO selectMemberById(String username);
}
