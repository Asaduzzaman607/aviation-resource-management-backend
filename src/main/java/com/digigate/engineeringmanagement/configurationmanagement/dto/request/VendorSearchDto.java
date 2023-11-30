package com.digigate.engineeringmanagement.configurationmanagement.dto.request;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.VendorWorkFlowType;
import com.digigate.engineeringmanagement.common.payload.SDto;
import com.digigate.engineeringmanagement.configurationmanagement.constant.VendorType;
import com.digigate.engineeringmanagement.storemanagement.constant.ApprovalSearchRequestType;
import lombok.Data;

@Data
public class VendorSearchDto implements SDto {
    private String query = ApplicationConstant.EMPTY_STRING;
    private ApprovalSearchRequestType type = ApprovalSearchRequestType.PENDING;
    private Boolean isActive = true;
    private VendorType vendorType;
    private VendorWorkFlowType workflowType;
}
