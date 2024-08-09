package com.multi.laptellect.recommend.laptop.model.dto;

import lombok.Data;

/**
 * Please explain the class!!
 *
 * @author : 이강석
 * @fileName : CriteriaDTO
 * @since : 2024-08-08
 */
@Data
public class CurationDTO {
    private String mainOption; // 게이밍 / 사무용 등 구분 공통 요소
    private String place; // 장소
    private String priority; // 작업 종류
    private String weight; // 원하는 무게

    // 게이밍
    private String game; // 주로 하는 게임
    private String screen; // 화면
    private String gameperformance; // 게이밍 가격

    //사무용
    private String purpose; // 사용목적
    private String performance; // 가격
    private String battery; // 배터리

    //
    private String internet; // 인터넷
    private String macseries; //데이터 처리
    private String macram;

}
