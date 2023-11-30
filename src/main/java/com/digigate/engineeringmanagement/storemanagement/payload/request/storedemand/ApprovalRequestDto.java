package com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class ApprovalRequestDto {

    private String rejectedDesc;
    private String approvalDesc;
    @NotNull
    private Boolean approve;
}
