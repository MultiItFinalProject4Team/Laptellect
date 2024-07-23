package com.multi.laptellect.api.auth;

import com.multi.laptellect.auth.service.AuthService;
import com.multi.laptellect.member.model.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api")
public class authApiController {
    private final AuthService authService;

    @ResponseBody
    @PostMapping("/check-id")
    public boolean isId(@RequestParam("userName") String id) {
        return authService.isMemberById(id);
    }

    @ResponseBody
    @PostMapping("/check-email")
    public boolean isEmail(@RequestParam("email") String email) {
        return authService.isMemberByEmail(email);
    }

    @ResponseBody
    @PostMapping("/check-nickname")
    public boolean isPassword(@RequestParam("nickName") String nickName) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        return authService.isMemberByNickName(nickName);
    }
}
