package com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand;

import com.digigate.engineeringmanagement.common.constant.ApprovalStatusType;
import com.digigate.engineeringmanagement.common.payload.IDto;
import com.digigate.engineeringmanagement.configurationmanagement.entity.administration.WorkFlowAction;
import lombok.*;

@Value(staticConstructor = "of")
public class ApprovalStatusDto implements IDto {
    Long parentId;
    ApprovalStatusType approvalStatusType;
    WorkFlowAction workFlowAction;
}
