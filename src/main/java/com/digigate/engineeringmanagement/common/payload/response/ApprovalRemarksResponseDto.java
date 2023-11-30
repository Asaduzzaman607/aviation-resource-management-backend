package com.digigate.engineeringmanagement.common.payload.response;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApprovalRemarksResponseDto {
    private String workFlowActionName;
    private LocalDate approvalDate;
    private String approvedBy;
    private String approvalRemark;
}

