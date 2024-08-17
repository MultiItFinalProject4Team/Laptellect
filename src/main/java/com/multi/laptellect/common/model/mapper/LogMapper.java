package com.multi.laptellect.common.model.mapper;

import com.multi.laptellect.admin.model.dto.LoginLog;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

/**
 * Please explain the class!!
 *
 * @author : 이강석
 * @fileName : LogMapper
 * @since : 2024-08-17
 */
@Mapper
public interface LogMapper {
    @Insert("INSERT INTO log_login ( member_no, login_ip, user_agent ) VALUES ( #{ memberNo }, #{ loginIp }, #{ userAgent })")
    void insertLoginLog(LoginLog loginLog);

    @Insert("INSERT INTO log_count_visit (created_at, visit_count) VALUES (CURDATE(), #{ count }) ON DUPLICATE KEY UPDATE visit_count = visit_count + #{ count }")
    int insertVisitCount(int count);
}