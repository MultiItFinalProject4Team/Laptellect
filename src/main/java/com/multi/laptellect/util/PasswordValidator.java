package com.multi.laptellect.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 비밀번호 유효성 검사
 *
 * @author : 이강석
 * @fileName : PasswordValidator
 * @since : 2024-08-22
 */
public class PasswordValidator {
    public static boolean validatePassword(String password) {
        String passwordPattern = "^(?=.*[a-zA-Z])(?=.*[!@#$%^*+=-_])(?=.*[0-9]).{8,15}$";
        Pattern pattern = Pattern.compile(passwordPattern);

        if (password == null) {
            return false; // null 방지
        }

        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }
}
