package com.multi.laptellect.util;

/**
 * 문자열 길이 체크 유틸 클래스
 *
 * @author : 이강석
 * @fileName : StringToVarcharSizeCheckUtil
 * @since : 2024-08-01
 */
public class StringToVarcharSizeCheckUtil {
    /**
     * 문자열이 VARCHAR 크기에 맞는지 검증
     *
     * @param str         문자열
     * @param varcharSize VARCHAR 사이즈
     * @return String 사이즈가 Varchar 사이즈보다 작으면 true, 크면 false 반환
     */
    public static boolean isVarcharSizeWithinString(String str, int varcharSize) {
        return str.length() < varcharSize;
    }
}
