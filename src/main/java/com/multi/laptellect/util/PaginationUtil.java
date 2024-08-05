package com.multi.laptellect.util;

import org.springframework.data.domain.Page;

/**
 * 페이징 처리 유틸 클래스
 *
 * @author : 이강석
 * @fileName : PaginationUtil
 * @since : 2024-08-04
 */
public class PaginationUtil {
    /**
     * 페이징 시 이전 버튼 조건을 반환
     *
     * @param objects 객체(DTO)
     * @param pageSize 표시할 페이지 수
     * @return the start page
     */
    public static int getStartPage(Page<?> objects, int pageSize) {
        int totalSize = objects.getTotalPages();
        int currentPage = objects.getNumber();

        if (totalSize <= pageSize) {
            return 0;
        } else if (currentPage < pageSize / 2) {
            return 0;
        } else if (currentPage >= totalSize - pageSize / 2) {
            return totalSize - pageSize;
        } else {
            return currentPage - (pageSize / 2);
        }
    }

    /**
     * 페이징 시 다음 버튼 조건을 반환
     *
     * @param objects 객체(DTO)
     * @param pageSize 표시할 페이지 수
     * @return the end page
     */
    public static int getEndPage(Page<?> objects, int pageSize) {
        int totalSize = objects.getTotalPages();
        int currentPage = objects.getNumber();

        if (totalSize <= pageSize) {
            return totalSize - 1;
        } else if (currentPage < pageSize / 2) {
            return pageSize - 1;
        } else if (currentPage >= totalSize - pageSize / 2) {
            return totalSize - 1;
        } else {
            return currentPage + (pageSize / 2);
        }
    }
}