//package com.multi.laptellect.util;
//
//import com.multi.mini.member.model.dto.CustomUserDetails;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//
//public class SecurityUtil {
//    public static CustomUserDetails getUserDetails() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        return (CustomUserDetails) authentication.getPrincipal();
//    }
//}
