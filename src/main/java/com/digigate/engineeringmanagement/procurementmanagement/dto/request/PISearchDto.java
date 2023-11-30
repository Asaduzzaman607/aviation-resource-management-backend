package com.digigate.engineeringmanagement.procurementmanagement.dto.request;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.payload.SDto;
import com.digigate.engineeringmanagement.procurementmanagement.constant.PartsInVoiceWorkFlowType;
import com.digigate.engineeringmanagement.procurementmanagement.constant.RfqType;
import com.digigate.engineeringmanagement.storemanagement.constant.ApprovalSearchRequestType;
import lombok.Data;

@Data
public class PISearchDto implements SDto {
    private String query = ApplicationConstant.EMPTY_STRING;
    private ApprovalSearchRequestType type = ApprovalSearchRequestType.PENDING;
    private RfqType rfqType;
    private PartsInVoiceWorkFlowType partsInVoiceWorkFlowType;
    private Boolean isActive = true;
}
