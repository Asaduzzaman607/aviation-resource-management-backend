package com.digigate.engineeringmanagement.configurationmanagement.dto.response.adminstration;

import com.digigate.engineeringmanagement.common.payload.IDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationEmployeeResponseDto implements IDto {
    private Long id;
    private Long employeeId;
    private NotificationSettingResponseDto notificationSetting;
}
