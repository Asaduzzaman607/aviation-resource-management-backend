package com.digigate.engineeringmanagement.storeinspector.payload.response.storeinspector;

import com.digigate.engineeringmanagement.common.payload.response.ApprovalRemarksResponseDto;
import com.digigate.engineeringmanagement.storemanagement.payload.response.storedemand.ApprovalStatusViewModel;
import lombok.*;

import java.util.List;
import java.util.Map;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InspectionChecklistResponseDto {
    private Long id;
    private String description;
    private Long workFlowActionId;
    private Integer workflowOrder;
    private String workflowName;
    private Boolean actionEnabled;
    private Boolean editable;
    private Map<Long, ApprovalStatusViewModel> approvalStatuses;
    private Map<Long, ApprovalStatusViewModel> qualityApprovalStatuses;
    private List<ApprovalRemarksResponseDto> approvalRemarksResponseDtoList;
    private List<ApprovalRemarksResponseDto> approvalRemarksResponseDtoListQuality;
}

