package com.multi.laptellect.util;

import com.multi.laptellect.member.model.dto.CustomUserDetails;
import com.multi.laptellect.member.model.dto.MemberDTO;
import com.multi.laptellect.member.model.mapper.MemberMapper;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 시큐리티 세션 정보를 가져오기 위한 Util 클래스
 *
 * @author : 이강석
 * @fileName : SecurityUtil.java
 * @since : 2024-07-26
 */
@Slf4j
public class SecurityUtil { // 시큐리티 세션 정보 가져오기 위한 클래스

    @Autowired
    private MemberMapper memberMapper;

    /**
     * 세션에서 사용자 정보를 가져오기 위한 메서드
     *
     * @return the user details
     */
    public static CustomUserDetails getUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            log.error("log error = {}", "로그인 하지 않은 사용자 입니다.");
            throw new IllegalStateException("로그인 하지 않은 사용자 입니다.");
        }

        return (CustomUserDetails) authentication.getPrincipal();
    }

    /**
     * Gets user no.
     *
     * @return the user no
     */
    public static int getUserNo() {
        return getUserDetails().getMemberNo();
    }

    /**
     * 로그인한 사용자의 세션을 업데이트 하기 위한 메서드
     *
     * @param memberDTO 업데이트 정보가 담긴 MemberDTO 객체
     */
    public static void updateUserDetails(MemberDTO memberDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        userDetails.update(memberDTO);

        Authentication updateAuthentication = new UsernamePasswordAuthenticationToken(userDetails, authentication.getCredentials(), authentication.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(updateAuthentication);

        // 세션 정보 강제 동기화
        HttpSession session = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getSession(false);
        if (session != null) {
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());
            log.info("Session updated with new authentication details");
            log.info("세션 업데이트 완료 = {}", memberDTO);
        }
    }

    /**
     * 로그인 된 사용자인지 체크
     *
     * @return the boolean
     */
    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        if (authentication.getPrincipal() instanceof String
                && authentication.getPrincipal().equals("anonymousUser")) {
            return false;
        }

        return true;
    }

    /**
     * 특정 권한을 가진 사용자인지 체크
     *
     * @param authority 권한 이름
     * @return true false 반환
     */
    public static boolean hasAuthority (String authority) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            return authentication.getAuthorities().contains(new SimpleGrantedAuthority(authority));
        }
        return false;
    }
}
