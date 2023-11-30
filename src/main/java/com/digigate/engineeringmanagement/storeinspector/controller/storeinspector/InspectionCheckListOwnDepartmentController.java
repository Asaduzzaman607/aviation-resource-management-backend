package com.digigate.engineeringmanagement.storeinspector.controller.storeinspector;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.VendorWorkFlowType;
import com.digigate.engineeringmanagement.common.payload.response.MessageResponse;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.configurationmanagement.dto.request.VendorSearchDto;
import com.digigate.engineeringmanagement.storeinspector.payload.request.storeinspector.InspectionChecklistRequestDto;
import com.digigate.engineeringmanagement.storeinspector.payload.response.storeinspector.InspectionChecklistResponseDto;
import com.digigate.engineeringmanagement.storeinspector.service.storeinspector.InspectionChecklistService;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand.ApprovalRequestDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.digigate.engineeringmanagement.common.constant.ApplicationConstant.*;

@RestController
@RequestMapping("/api/store-inspector/own_department/inspection-checklists")
public class InspectionCheckListOwnDepartmentController {
    private final InspectionChecklistService inspectionChecklistService;

    public InspectionCheckListOwnDepartmentController(InspectionChecklistService inspectionChecklistService) {
        this.inspectionChecklistService = inspectionChecklistService;
    }

    @PostMapping
    public ResponseEntity<MessageResponse> create(@Valid @RequestBody InspectionChecklistRequestDto inspectionChecklistRequestDto) {
        return ResponseEntity.ok(new MessageResponse(CREATED_SUCCESSFULLY_MESSAGE, inspectionChecklistService.create(inspectionChecklistRequestDto).getId()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MessageResponse> update(@Valid @RequestBody InspectionChecklistRequestDto inspectionChecklistRequestDto, @PathVariable Long id) {
        return ResponseEntity.ok(new MessageResponse(UPDATED_SUCCESSFULLY_MESSAGE, inspectionChecklistService.update(inspectionChecklistRequestDto, id).getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InspectionChecklistResponseDto> getSingle(@PathVariable Long id) {
        InspectionChecklistResponseDto inspectionChecklistResponseDto = inspectionChecklistService.getSingle(id);
        return new ResponseEntity<>(inspectionChecklistResponseDto, HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<MessageResponse> updateActiveStatus(@PathVariable Long id, @RequestParam("active") Boolean isActive) {
        inspectionChecklistService.updateActiveStatus(id, isActive, VendorWorkFlowType.OWN_DEPARTMENT);
        return ResponseEntity.ok(new MessageResponse(STATUS_CHANGED));
    }

    @PostMapping("/search")
    public ResponseEntity<PageData> search(@RequestBody @Valid VendorSearchDto searchDto,
                                           @PageableDefault(sort = ApplicationConstant.DEFAULT_SORT,
                                                   direction = Sort.Direction.ASC) Pageable pageable) {
        return new ResponseEntity<>(inspectionChecklistService.search(searchDto, pageable), HttpStatus.OK);
    }

    @PutMapping("/decide/{id}")
    public ResponseEntity<String> makeDecision(@PathVariable Long id, @Valid @RequestBody ApprovalRequestDto approvalRequestDto) {
        inspectionChecklistService.makeDecision(id, approvalRequestDto, VendorWorkFlowType.OWN_DEPARTMENT);
        return ResponseEntity.ok(STATUS_CHANGED);
    }
}
