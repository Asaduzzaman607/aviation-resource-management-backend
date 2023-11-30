package com.digigate.engineeringmanagement.storemanagement.payload.response.storedemand;

import com.digigate.engineeringmanagement.common.payload.response.ApprovalRemarksResponseDto;
import com.digigate.engineeringmanagement.planning.payload.response.PartViewModel;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StoreReturnResponseDto {
    private Long id;
    private String voucherNo;
    private Boolean isInternalDept;
    private Set<String> attachment;
    private String aircraftRegistration;
    private Boolean isActive;
    private Long departmentId;
    private String departmentName;
    private Long locationId;
    private String locationCode;
    private String stockRoomType;
    private Long storeStockRoomId;
    private Integer partClassification;
    private String storeReturnStatusType;
    private String stockRoomName;
    private Boolean isServiceable;
    private String workOrderNumber;
    private String workOrderSerial;
    private Long officeId;
    private String officeCode;
    private Long storeIssueId;
    private String storeIssueVoucherNo;
    private Long submittedById;
    private Long returningOfficerId;
    private Long workFlowActionId;
    private Integer workflowOrder;
    private String workflowName;
    private String remarks;
    private Boolean actionEnabled;
    private Boolean editable;
    private List<StoreReturnPartResponseDto> storeReturnPartList;
    private Map<Long, ApprovalStatusViewModel> approvalStatuses;
    private List<ApprovalRemarksResponseDto> approvalRemarksResponseDtoList;
    private Boolean isRejected;
    private String rejectedDesc;
    private LocalDate createDate;
    private List<PartViewModel> partViewModels;
    private Boolean isActiveCountZero;
    private String storeLocation;
}
