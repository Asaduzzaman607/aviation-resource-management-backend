package com.digigate.engineeringmanagement.configurationmanagement.dto.request.administration;

import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.payload.IDto;
import com.digigate.engineeringmanagement.configurationmanagement.entity.administration.WorkFlowAction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalSettingDto implements IDto {

    private Long id;
    @NotNull(message = ErrorId.WORK_FLOW_ACTION_NOT_FOUND)
    private Long workFlowActionId;
    @NotNull(message = ErrorId.SUB_MODULE_ID_REQUIRED)
    private Long subModuleItemId;
    @NotEmpty(message = ErrorId.EMPLOYEE_ID_REQUIRED)
    private Set<Long> employeeIds;
}
