package com.multi.laptellect.api.member;

import com.multi.laptellect.member.model.dto.AddressDTO;
import com.multi.laptellect.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 회원 API 컨트롤러
 *
 * @author : 이강석
 * @fileName : MemberApiController
 * @since : 2024-08-01
 */
@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberApiController {
    private final MemberService memberService;

    /**
     * 회원 배송지 Insert
     *
     * @param addressDTO 배송지 정보가 담긴 DTO 객체
     * @return the boolean
     */
    @ResponseBody
    @PostMapping("/insert-address")
    public int createMemberAddress(@RequestBody AddressDTO addressDTO){
        int result = 1;
        
        try {
            result = memberService.createMemberAddress(addressDTO);
        } catch (Exception e) {
            log.error("배송지 Insert 실패 = ", e);
            return 500;
        }

        return result;
    }
}
