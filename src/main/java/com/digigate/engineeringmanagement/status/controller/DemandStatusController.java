package com.digigate.engineeringmanagement.status.controller;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.status.dto.request.DemandStatusRequestDto;
import com.digigate.engineeringmanagement.status.service.DemandStatusService;
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
@RequestMapping("/api/store-demand/status")
public class DemandStatusController {

    private final DemandStatusService demandStatusService;

    public DemandStatusController(DemandStatusService demandStatusService) {
        this.demandStatusService = demandStatusService;
    }

    @PostMapping("/part")
    public ResponseEntity<?> getDemandStatusById(@RequestBody DemandStatusRequestDto demandStatusRequestDto, @PageableDefault(
            sort = ApplicationConstant.DEFAULT_SORT,
            direction = Sort.Direction.ASC) Pageable pageable) {
        PageData demandStatusResponseDto = demandStatusService.getDemandStatusInfo(demandStatusRequestDto, pageable);
        return new ResponseEntity<>(demandStatusResponseDto, HttpStatus.OK);
    }
}
