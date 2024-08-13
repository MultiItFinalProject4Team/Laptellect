package com.multi.laptellect.product.model.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

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


    @Min(0) //Validation 데이터 무결성 보장을 위한 어노테이션
    private int page; // 페이지 번호

    @Min(1)
    private int size; // 페이징 할 개수 (10개, 20개, 30개 등)

    private Sort sort; // 정렬 기준

    // 검색을 위한 변수
    @NotNull
    private int typeNo; // 상품 카테고리 번호
    private List<String> cate; // 카테고리 옵션
    private String keyword; // 검색어




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
        return (long) this.page * this.size;
    }

    @Override
    public Sort getSort() { // 정렬 기준
        return this.sort;
    }

    @Override
    public Pageable next() { // JPA꺼 무시하면 됨
        return new ProductSearchDTO(this.page + 1, this.size, this.sort, this.typeNo, this.cate, this.keyword);
    }

    @Override
    public Pageable previousOrFirst() { // JPA꺼 무시하면 됨
        return hasPrevious() ? new ProductSearchDTO(this.page - 1, this.size, this.sort, this.typeNo, this.cate, this.keyword) : this;
    }

    @Override
    public Pageable first() { // JPA꺼 무시하면 됨
        return new ProductSearchDTO(0, this.size, this.sort, this.typeNo, this.cate, this.keyword);
    }

    @Override
    public Pageable withPage(int pageNumber) { // JPA꺼 무시하면 됨
        return new ProductSearchDTO(pageNumber, this.size, this.sort, this.typeNo, this.cate, this.keyword);
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