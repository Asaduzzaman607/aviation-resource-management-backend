package com.digigate.engineeringmanagement.configurationmanagement.dto.request.administration;

import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.payload.IDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalEmployeeDto implements IDto {

    private Long id;
    @NotNull(message = ErrorId.APPROVAL_ITEM_ID_REQUIRED)
    private Long ApprovalSettingId;
    @NotNull(message = ErrorId.EMPLOYEMEE_ID_REQUIRED)
    private Long employeeId;
}
