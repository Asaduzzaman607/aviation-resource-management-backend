package com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.payload.SDto;
import com.digigate.engineeringmanagement.procurementmanagement.constant.OrderType;
import com.digigate.engineeringmanagement.storemanagement.constant.ApprovalSearchRequestType;
import com.digigate.engineeringmanagement.storemanagement.constant.PriorityType;
import lombok.Data;

@Data
public class ProcurementRequisitionCustomSearchDto implements SDto {
    private Long rfqId;
    private String query = ApplicationConstant.EMPTY_STRING;
    private ApprovalSearchRequestType type = ApprovalSearchRequestType.PENDING;
    private OrderType orderType = OrderType.PURCHASE;
    private Boolean isActive = true;
    private Boolean notIssued;
    private Boolean notRequisition;
    private Boolean isAlive;
    private String partNo;
    private String aircraftName;
    private String demandBy;
    private PriorityType priority;
}
