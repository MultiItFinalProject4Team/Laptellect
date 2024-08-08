package com.multi.laptellect.common.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Please explain the class!!
 *
 * @author : 이강석
 * @fileName : PaginationDTO
 * @since : 2024-08-07
 */

@Getter
@Setter
public class PaginationDTO {
    private int page;
    private String startDate; // 시작 날짜
    private String endDate; // 종료 날짜
    private String selectType; // 선택 유형
    private String keyword; // 검색 키워드
}
