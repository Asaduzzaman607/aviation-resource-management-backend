package com.digigate.engineeringmanagement.configurationmanagement.controller.administration;

import com.digigate.engineeringmanagement.common.controller.AbstractController;
import com.digigate.engineeringmanagement.configurationmanagement.dto.request.administration.NotificationSettingRequestDto;
import com.digigate.engineeringmanagement.configurationmanagement.entity.administration.NotificationSetting;
import com.digigate.engineeringmanagement.configurationmanagement.service.administration.NotificationSettingService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notification-settings")
public class NotificationSettingController extends AbstractController<NotificationSetting, NotificationSettingRequestDto> {
    public NotificationSettingController(NotificationSettingService service) {
        super(service);
    }
}