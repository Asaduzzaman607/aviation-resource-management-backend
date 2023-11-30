package com.digigate.engineeringmanagement.procurementmanagement.dto.response;


import com.digigate.engineeringmanagement.common.payload.response.ApprovalRemarksResponseDto;
import com.digigate.engineeringmanagement.procurementmanagement.constant.InputType;
import com.digigate.engineeringmanagement.procurementmanagement.constant.RfqType;
import com.digigate.engineeringmanagement.storemanagement.payload.response.storedemand.ApprovalStatusViewModel;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RfqVendorResponseDto {
    private Long id;
    private String rfqNo;
    private Long requisitionId;
    private Long partOrderId;
    private String orderNo;
    private String voucherNo;
    private RfqType rfqType;
    private InputType inputType;
    private List<QrVendorViewModel> quoteRequestVendorModelList;
    private Long workFlowActionId;
    private Integer workflowOrder;
    private String workflowName;
    private Boolean actionEnabled;
    private Boolean editable;
    private Boolean isRejected;
    private String rejectedDesc;
    private Map<Long, ApprovalStatusViewModel> approvalStatuses;
    private List<ApprovalRemarksResponseDto> approvalRemarksResponseDtoList;

}
