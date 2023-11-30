package com.digigate.engineeringmanagement.procurementmanagement.dto.response;

import com.digigate.engineeringmanagement.common.payload.response.ApprovalRemarksResponseDto;
import com.digigate.engineeringmanagement.procurementmanagement.constant.CsWorkflowType;
import com.digigate.engineeringmanagement.storemanagement.payload.response.storedemand.ApprovalStatusViewModel;
import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CsMainResponseDto {

    private Long id;
    private String csNo;
    private Long rfqId;
    private String rfqNo;
    private Long submittedId;
    private String submittedByName;
    private Boolean isRejected;
    private String rejectedDesc;

    private Long workFlowActionId;
    private Integer workflowOrder;
    private String workflowName;
    private CsWorkflowType workflowType;
    private Map<Long, ApprovalStatusViewModel> approvalStatuses;
    private Map<Long, ApprovalStatusViewModel> auditApprovalStatuses;
    private Map<Long, ApprovalStatusViewModel> finalApprovalStatuses;
    private List<ApprovalRemarksResponseDto> approvalRemarksResponseDtoList;
    private List<ApprovalRemarksResponseDto> approvalRemarksResponseDtoListAudit;
    private List<ApprovalRemarksResponseDto> approvalRemarksResponseDtoListFinal;
    private Boolean editable;
    private Boolean actionEnabled;

    private List<CsDetailResponseDto> csDetailResponseDtoList;
    private List<CsPartDetailResponseDto> csPartDetailResponseDtoList;
}
