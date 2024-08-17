package com.multi.laptellect.common.service;

import com.multi.laptellect.admin.model.dto.LoginLog;
import com.multi.laptellect.common.model.mapper.LogMapper;
import com.multi.laptellect.member.model.mapper.MemberMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Please explain the class!!
 *
 * @author : 이강석
 * @fileName : LogServiceImpl
 * @since : 2024-08-17
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LogServiceImpl implements LogService{
    private final MemberMapper memberMapper;
    private final LogMapper logMapper;

    @Override
    public void saveLoginLog(HttpServletRequest request, String userName) throws Exception {
        LoginLog loginLog = new LoginLog();
        loginLog.setMemberNo(memberMapper.findMemberById(userName).getMemberNo());
        loginLog.setLoginIp(request.getRemoteAddr());
        loginLog.setUserAgent(request.getHeader("User-Agent"));

        logMapper.insertLoginLog(loginLog);
        log.info("로그인 로그 저장 성공 = {}", userName);
    }

    @Override
    public void insertVisitCount(int count) throws Exception{
        int result = logMapper.insertVisitCount(count);
        log.info("방문자 수 저장 성공 = {}", count);
    }
}
