package com.digigate.engineeringmanagement.storemanagement.payload.response.storedemand;

import com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand.StorePartLoanDetailDto;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StorePartLoanResponseDto {
    private Long id;
    private String loanNo;
    private LocalDate loanExpires;
    private LocalDate updateDate;
    private String attachment;
    private Long externalDepartmentId;
    private String externalDepartmentName;
    private Long workFlowActionId;
    private Integer workflowOrder;
    private String workflowName;
    private String remarks;
    private List<StorePartLoanDetailDto> storePartLoanDetailDtoList;
    private Map<Long, ApprovalStatusViewModel> approvalStatuses;
    private Boolean editable;
    private Boolean actionEnabled;
    private Boolean isRejected;
    private String rejectedDesc;
}
