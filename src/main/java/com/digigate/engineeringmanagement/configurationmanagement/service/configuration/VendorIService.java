package com.digigate.engineeringmanagement.configurationmanagement.service.configuration;

import com.digigate.engineeringmanagement.common.constant.VendorWorkFlowType;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.configurationmanagement.dto.request.VendorSearchDto;
import com.digigate.engineeringmanagement.configurationmanagement.dto.request.configuration.VendorDto;
import com.digigate.engineeringmanagement.configurationmanagement.dto.response.VendorViewModel;
import com.digigate.engineeringmanagement.configurationmanagement.entity.Vendor;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand.ApprovalRequestDto;
import org.springframework.data.domain.Pageable;

public interface VendorIService {
     Vendor create(VendorDto vendorDto);

    Vendor update(VendorDto vendorDto, Long id);

    VendorViewModel getSingle(Long id);

    PageData search(VendorSearchDto dto, Pageable pageable);

    void makeDecision(Long id, ApprovalRequestDto approvalRequestDto, VendorWorkFlowType vendorWorkFlowType);

    void updateActiveStatus(Long id, Boolean isActive, VendorWorkFlowType vendorWorkFlowType);
}
