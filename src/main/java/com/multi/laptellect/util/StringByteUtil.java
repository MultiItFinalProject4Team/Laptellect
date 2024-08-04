package com.multi.laptellect.util;

import java.nio.charset.StandardCharsets;

/**
 * 문자열 길이 체크 유틸 클래스
 *
 * @author : 이강석
 * @fileName : StringByteUtil
 * @since : 2024-08-01
 */
public class StringByteUtil {
    /**
     * 문자열 Byte 계산하는 메서드
     *
     * @param str 문자열
     * @return 문자열 바이트 값
     */
    public static int getStringSizeInBytes(String str) {
        return str.getBytes(StandardCharsets.UTF_8).length;
    }

    /**
     * 문자열이 VARCHAR 크기에 맞는지 검증하는 메서드
     *
     * @param str         문자열
     * @param varcharSize VARCHAR 사이즈
     * @return 결과 값 bool
     */
    public static boolean isValidByteSize(String str, int varcharSize) {
        int sizeInByte = getStringSizeInBytes(str);
        return sizeInByte <= varcharSize;
    }
}
