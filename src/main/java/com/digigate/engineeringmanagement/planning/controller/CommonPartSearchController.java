package com.digigate.engineeringmanagement.planning.controller;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.planning.payload.request.CommonPartSearchDto;
import com.digigate.engineeringmanagement.planning.service.CommonPartSearchService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/common-part")
public class CommonPartSearchController {
    private final CommonPartSearchService commonPartSearchService;

    public CommonPartSearchController(CommonPartSearchService commonPartSearchService) {
        this.commonPartSearchService = commonPartSearchService;
    }

    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody CommonPartSearchDto commonPartSearchDto, @PageableDefault(
            sort = ApplicationConstant.DEFAULT_SORT,
            direction = Sort.Direction.ASC) Pageable pageable){
        return new ResponseEntity<>(commonPartSearchService.search(commonPartSearchDto, pageable), HttpStatus.OK);
    }

}
