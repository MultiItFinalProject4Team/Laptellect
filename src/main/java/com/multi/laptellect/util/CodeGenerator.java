package com.multi.laptellect.util;

import java.util.Random;

public class CodeGenerator { // 코드 자동 생성 클래스
    private static final char[] codeChars = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
                                              'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
                                              '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

    public static String createRandomString(int size) { // 랜덤 코드 생성
        Random random = new Random();

        StringBuilder randomString = new StringBuilder(size);

        for (int i = 0; i < size; i++) {
            int randomCode = random.nextInt(codeChars.length);
            randomString.append(codeChars[randomCode]);
        }

        return randomString.toString();
    }
}
