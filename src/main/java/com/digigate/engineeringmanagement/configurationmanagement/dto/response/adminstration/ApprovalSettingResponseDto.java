package com.digigate.engineeringmanagement.configurationmanagement.dto.response.adminstration;

import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.payload.IDto;
import com.digigate.engineeringmanagement.configurationmanagement.entity.administration.WorkFlowAction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalSettingResponseDto implements IDto {
    private Long id;
    private String workFlowActionName;
    private Long workFlowActionId;
    private Long subModuleItemId;
    private String subModuleItemName;
}

