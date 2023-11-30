package com.digigate.engineeringmanagement.storemanagement.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import static com.digigate.engineeringmanagement.common.constant.ApplicationConstant.CREATED_DATE;

public class SortChanger {
    public static Pageable descendingSortByCreatedAt(Pageable pageable){
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(CREATED_DATE).descending());
    }
}
