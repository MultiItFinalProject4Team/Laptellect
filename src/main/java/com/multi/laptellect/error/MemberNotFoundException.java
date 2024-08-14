package com.multi.laptellect.error;

import org.springframework.security.core.AuthenticationException;

/**
 * Please explain the class!!
 *
 * @author : 이강석
 * @fileName : UserActiveException
 * @since : 2024-08-13
 */
public class MemberNotFoundException extends AuthenticationException {

    public MemberNotFoundException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public MemberNotFoundException(String msg) {
        super(msg);
    }
}
