package com.multi.laptellect.customer.service;

import com.multi.laptellect.customer.dto.NoticeListDto;
import com.multi.laptellect.customer.dto.PersonalqListDto;
import com.multi.laptellect.customer.dto.ProductqList;
import com.multi.laptellect.customer.dto.ProuductqListDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaginationService {
    public List<PersonalqListDto> personalpaginate(List<PersonalqListDto> list, int page, int pageSize) {
        int start = page * pageSize;
        int end = Math.min(start + pageSize, list.size());
        return list.subList(start, end);
    }
    public List<NoticeListDto> noticepaginate(List<NoticeListDto> list, int page, int pageSize) {
        int start = page * pageSize;
        int end = Math.min(start + pageSize, list.size());
        return list.subList(start, end);
    }

    public List<ProuductqListDto> productpaginate(List<ProuductqListDto> list, int page, int pageSize) {
        int start = page * pageSize;
        int end = Math.min(start + pageSize, list.size());
        return list.subList(start, end);
    }

    public List<ProductqList> productpaginate2(List<ProductqList> list, int page, int pageSize) {
        int start = page * pageSize;
        int end = Math.min(start + pageSize, list.size());
        return list.subList(start, end);
    }
}
