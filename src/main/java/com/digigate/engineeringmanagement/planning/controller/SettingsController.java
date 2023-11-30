package com.digigate.engineeringmanagement.planning.controller;

import com.digigate.engineeringmanagement.common.controller.AbstractController;
import com.digigate.engineeringmanagement.common.service.IService;
import com.digigate.engineeringmanagement.planning.entity.Settings;
import com.digigate.engineeringmanagement.planning.payload.request.SettingsDto;
import com.digigate.engineeringmanagement.planning.service.SettingsService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/settings")
public class SettingsController extends AbstractController<Settings, SettingsDto> {

    public SettingsController(IService<Settings, SettingsDto> service, SettingsService settingsService) {
        super(service);
    }
}
