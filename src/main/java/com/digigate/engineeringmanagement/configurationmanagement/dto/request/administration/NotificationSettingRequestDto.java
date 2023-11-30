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
@AllArgsConstructor
@NoArgsConstructor
public class NotificationSettingRequestDto implements IDto {
    private Long workflowActionId;
    @NotNull(message = ErrorId.SUBMODULE_ITEM_ID_REQUIRED)
    private Long submoduleItemId;
    @NotEmpty(message = ErrorId.EMPLOYEE_ID_REQUIRED)
    private Set<Long> employeeIds;
}
