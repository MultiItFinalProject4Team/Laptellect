package com.multi.laptellect.customer.service;

import com.multi.laptellect.customer.dto.PersonalqListDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaginationService {
    public int pageSize=10;
    public List<PersonalqListDto> paginate(List<PersonalqListDto> list, int page, int pageSize) {
        int start = page * pageSize;
        int end = Math.min(start + pageSize, list.size());
        return list.subList(start, end);
    }
}
