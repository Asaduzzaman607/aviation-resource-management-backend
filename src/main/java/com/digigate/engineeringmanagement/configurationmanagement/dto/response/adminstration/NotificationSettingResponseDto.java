package com.digigate.engineeringmanagement.configurationmanagement.dto.response.adminstration;

import com.digigate.engineeringmanagement.configurationmanagement.entity.administration.WorkFlowAction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationSettingResponseDto {
    private Long id;
    private String workflowActionName;
    private Long submoduleItemId;
}