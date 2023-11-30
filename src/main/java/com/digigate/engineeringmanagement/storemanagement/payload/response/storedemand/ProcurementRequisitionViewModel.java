package com.digigate.engineeringmanagement.storemanagement.payload.response.storedemand;

import com.digigate.engineeringmanagement.common.payload.response.ApprovalRemarksResponseDto;
import com.digigate.engineeringmanagement.storemanagement.constant.DepartmentType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@NoArgsConstructor
public class ProcurementRequisitionViewModel {
    private Long id;
    private String voucherNo;
    private Long storeDemandId;
    private String storeDemandNo;
    private DepartmentType departmentType;
    private Long workFlowActionId;
    private Integer workflowOrder;
    private String workflowName;
    private String remarks;
    private Set<String> attachment;
    private Boolean actionEnabled;
    private Boolean editable;
    private List<ProcurementRequisitionItemViewModel> requisitionItemViewModels;
    private Map<Long, ApprovalStatusViewModel> approvalStatuses;
    private List<ApprovalRemarksResponseDto> approvalRemarksResponseDtoList;
    private Boolean isRejected;
    private String rejectedDesc;
    private LocalDate createdDate;
}
