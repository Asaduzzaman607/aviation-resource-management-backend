package com.digigate.engineeringmanagement.configurationmanagement.controller.configuration;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.VendorWorkFlowType;
import com.digigate.engineeringmanagement.common.payload.response.MessageResponse;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.configurationmanagement.dto.request.VendorSearchDto;
import com.digigate.engineeringmanagement.configurationmanagement.dto.request.configuration.VendorDto;
import com.digigate.engineeringmanagement.configurationmanagement.dto.response.VendorViewModel;
import com.digigate.engineeringmanagement.configurationmanagement.service.configuration.ShipmentProviderService;
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
@RequestMapping("/api/material-management/config/shipment_provider")
public class ShipmentProviderOwnDepartmentController {
    private final ShipmentProviderService shipmentProviderService;

    public ShipmentProviderOwnDepartmentController(ShipmentProviderService shipmentProviderService) {
        this.shipmentProviderService = shipmentProviderService;
    }

    @PostMapping
    public ResponseEntity<MessageResponse> create(@Valid @RequestBody VendorDto vendorDto) {
        return ResponseEntity.ok(new MessageResponse(CREATED_SUCCESSFULLY_MESSAGE, shipmentProviderService.create(vendorDto).getId()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MessageResponse> update(@Valid @RequestBody VendorDto vendorDto, @PathVariable Long id) {
        return ResponseEntity.ok(new MessageResponse(UPDATED_SUCCESSFULLY_MESSAGE, shipmentProviderService.update(vendorDto, id).getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VendorViewModel> getSingle(@PathVariable Long id) {
        VendorViewModel vendorViewModel = shipmentProviderService.getSingle(id);
        return new ResponseEntity<>(vendorViewModel, HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<MessageResponse> updateActiveStatus(@PathVariable Long id,
                                                              @RequestParam("active") Boolean isActive) {
        shipmentProviderService.updateActiveStatus(id, isActive, VendorWorkFlowType.OWN_DEPARTMENT);
        return ResponseEntity.ok(new MessageResponse(STATUS_CHANGED));
    }

    @PostMapping("/search")
    public ResponseEntity<PageData> search(@RequestBody @Valid VendorSearchDto searchDto,
                                           @PageableDefault(
                                                   sort = ApplicationConstant.DEFAULT_SORT,
                                                   direction = Sort.Direction.DESC) Pageable pageable) {
        return new ResponseEntity<>(shipmentProviderService.search(searchDto, pageable), HttpStatus.OK);
    }

    @PutMapping("/decide/{id}")
    public ResponseEntity<String> makeDecision(@PathVariable Long id, @Valid @RequestBody
    ApprovalRequestDto approvalRequestDto) {
        shipmentProviderService.makeDecision(id, approvalRequestDto, VendorWorkFlowType.OWN_DEPARTMENT);
        return ResponseEntity.ok(STATUS_CHANGED);
    }
}


