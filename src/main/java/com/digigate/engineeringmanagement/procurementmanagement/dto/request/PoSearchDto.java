package com.digigate.engineeringmanagement.procurementmanagement.dto.request;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.payload.SDto;
import com.digigate.engineeringmanagement.procurementmanagement.constant.OrderType;
import com.digigate.engineeringmanagement.procurementmanagement.constant.RfqType;
import com.digigate.engineeringmanagement.storemanagement.constant.ApprovalSearchRequestType;
import lombok.Data;

@Data
public class PoSearchDto implements SDto {
    private String query = ApplicationConstant.EMPTY_STRING;
    private ApprovalSearchRequestType type = ApprovalSearchRequestType.PENDING;
    private RfqType rfqType;
    private OrderType orderType;
    private Boolean isActive = true;
}
