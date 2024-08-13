package com.multi.laptellect.customer.dto;

import lombok.Data;

import java.util.List;

@Data
public class PageResponse<T> {
    private List<T> content;
    private int totalPages;

    public PageResponse(List<T> content, int totalPages) {
        this.content = content;
        this.totalPages = totalPages;
    }
}
