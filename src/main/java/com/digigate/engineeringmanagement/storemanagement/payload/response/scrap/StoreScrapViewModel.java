package com.digigate.engineeringmanagement.storemanagement.payload.response.scrap;

import com.digigate.engineeringmanagement.common.payload.response.ApprovalRemarksResponseDto;
import com.digigate.engineeringmanagement.storemanagement.payload.request.scrap.StoreScrapPartDto;
import com.digigate.engineeringmanagement.storemanagement.payload.response.storedemand.ApprovalStatusViewModel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@NoArgsConstructor
public class StoreScrapViewModel {
    private Long id;
    private String voucherNo;
    private Long workFlowActionId;
    private Long submittedById;
    private String submittedBy;
    private Integer workflowOrder;
    private String workflowName;

    private List<StoreScrapPartViewModel> storeScrapPartViewModels;
    private Map<Long, ApprovalStatusViewModel> approvalStatuses;
    private List<ApprovalRemarksResponseDto> approvalRemarksResponseDtoList;
    private Boolean editable;
    private Boolean actionEnabled;
    private String remarks;
    private Boolean isRejected;
    private String rejectedDesc;
    private Set<String> attachmentList = new HashSet<>();
}
