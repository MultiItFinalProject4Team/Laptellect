package com.multi.laptellect.util;

/**
 * 아이디 검증 로직
 *
 * @author : 이강석
 * @fileName : UserValidator
 * @since : 2024-08-22
 */
public class UserValidator {
    public static String validateUserId(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            return "아이디를 입력해주세요.";
        } else if (userId.length() < 4) {
            return "아이디는 최소 4글자 이상이어야 합니다.";
        } else if (!userId.matches("^[a-zA-Z0-9]+$")) {
            return "아이디는 특수문자 또는 한글을 사용할 수 없습니다.";
        } else if (userId.matches("^\\d+$")) {
            return "아이디는 숫자만 사용할 수 없습니다.";
        } else {
            return "success";
        }
    }
}
