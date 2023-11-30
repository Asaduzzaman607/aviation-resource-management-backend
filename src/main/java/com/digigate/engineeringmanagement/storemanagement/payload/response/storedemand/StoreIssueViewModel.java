package com.digigate.engineeringmanagement.storemanagement.payload.response.storedemand;

import com.digigate.engineeringmanagement.common.payload.response.ApprovalRemarksResponseDto;
import com.digigate.engineeringmanagement.planning.constant.PartClassification;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand.StoreIssueItemResponseDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StoreIssueViewModel {
    private Long id;
    private String voucherNo;
    private Long storeDemandId;
    private String storeDemandNo;
    private Long storeStockRoomId;
    private String storeStockRoom;
    private String stockRoomType;
    private PartClassification partClassification;
    private String registration;
    private Long workFlowActionId;
    private Integer workflowOrder;
    private String workflowName;
    private String remarks;
    private Boolean actionEnabled;
    private Boolean editable;
    private List<StoreIssueItemResponseDto> storeIssueItemResponseDtos;
    private List<ApprovalRemarksResponseDto> approvalRemarksResponseDtoList;
    private Map<Long, ApprovalStatusViewModel> approvalStatuses;
    private Boolean isRejected;
    private String rejectedDesc;
    private LocalDate createdDate;
    private Boolean isReturnApproved;
}
