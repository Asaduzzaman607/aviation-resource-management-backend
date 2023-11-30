package com.digigate.engineeringmanagement.planning.service;

import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.planning.payload.request.CommonPartSearchDto;
import org.springframework.data.domain.Pageable;

public interface CommonPartSearchService {
    PageData search(CommonPartSearchDto commonPartSearchDto, Pageable pageable);
}
