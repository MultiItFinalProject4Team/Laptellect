package com.multi.laptellect.product.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Map;

/**
 * Please explain the class!!
 *
 * @author : 이우석
 * @fileName : ProductSearchDTO
 * @since : 2024-08-08
 */
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class ProductSearchDTO implements Pageable {
    // 페이징을 위한 기본 변수



    private int page = 0; // 페이지 번호


    private int size; // 페이징 할 개수 (10개, 20개, 30개 등)

    private Sort sort; // 정렬 기준

    // 검색을 위한 변수
    private int typeNo; // 상품 카테고리 번호
    private Map<String, List<String>> cate; // 카테고리 옵션
    private String keyword; // 검색어
    private List<String> LBI1; // 운영체제 필터
    private List<String> LBI2; // 제조사 필터
    private List<String> LS22; // 저장장치 종류
    private List<String> LS21; // 저장 용량
    private List<String> LG38; // GPU 종류
    private List<String> LC13; // CPU 종류
    private List<String> LR9; // 램 용량
    private List<String> LD25; // 해상도

    private Map<String,List<String>> cate1;


    private Integer minPrice;
    private Integer maxPrice;




    @Override
    public int getPageNumber() { // 페이지 넘버
        return this.page;
    }

    @Override
    public int getPageSize() { // 가져올 페이지 사이즈
        return this.size;
    }

    @Override
    public long getOffset() { // 페이징 시작할 위치
        return (long) (this.page) * this.size;
    }

    @Override
    public Sort getSort() { // 정렬 기준
        return this.sort;
    }

    @Override
    public Pageable next() { // JPA꺼 무시하면 됨
        return null;
    }

    @Override
    public Pageable previousOrFirst() { // JPA꺼 무시하면 됨
        return null;
    }

    @Override
    public Pageable first() { // JPA꺼 무시하면 됨
        return null;
    }

    @Override
    public Pageable withPage(int pageNumber) { // JPA꺼 무시하면 됨
        return null;
    }

    @Override
    public boolean hasPrevious() {  // 이전 페이지 있는지 조회
        return this.page > 0;
    }
}

/* Ajax로 보낼 떄 예시
let data = {
    page: 1,
    size: 10,
    sort: {
        property: "name",
        direction: "ASC"
    },
    typeNo: 2,
    Cate: ["option1", "option2", "option3"],
    keyword: "example"
};

$.ajax({
    type: "POST",
    url: "/product/search",
    contentType: "application/json",
    data: JSON.stringify(data),
    success: function(response) {
        console.log("Success:", response);
    },
    error: function(error) {
        console.error("Error:", error);
    }
});
*/