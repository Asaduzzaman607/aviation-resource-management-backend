package com.digigate.engineeringmanagement.configurationmanagement.dto.request.search;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class SelectedUserSearchDto {
    @NotNull
    private Long workFlowActionId;
    private Boolean isActive = true;
    @NotNull
    private Long subModuleItemId;
    private Long designationId;
    private Long sectionId;
    private Long departmentId;
}
