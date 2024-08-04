package com.multi.laptellect.util;

import org.springframework.data.domain.Page;

/**
 * 페이징 처리 유틸 클래스
 *
 * @author : 이강석
 * @fileName : PaginationUtils
 * @since : 2024-08-04
 */
public class PaginationUtil {
    public static int getStartPage(Page<?> objects) {
        int totalSize = objects.getTotalPages();
        int currentPage = objects.getNumber();

        if (totalSize < 5) {
            return 0;
        } else if (currentPage <= 2) {
            return 0;
        } else if (currentPage >= totalSize - 3) {
            return totalSize - 5;
        } else {
            return currentPage - 2;
        }
    }

    public static int getEndPage(Page<?> objects) {
        int totalSize = objects.getTotalPages();
        int currentPage = objects.getNumber();

        if (totalSize < 5) {
            return totalSize - 1;
        } else if (currentPage <= 2) {
            return 4;
        } else if (currentPage >= totalSize - 3) {
            return totalSize - 1;
        } else {
            return currentPage + 2;
        }
    }
}
