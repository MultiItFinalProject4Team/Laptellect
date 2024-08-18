package com.multi.laptellect.common.service;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Please explain the class!!
 *
 * @author : 이강석
 * @fileName : LogService
 * @since : 2024-08-17
 */
public interface LogService {
    void saveLoginLog(HttpServletRequest request, String userName) throws Exception;

    void insertVisitCount(int count) throws Exception;
}
