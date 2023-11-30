package com.digigate.engineeringmanagement.configurationmanagement.controller.configuration;

import com.digigate.engineeringmanagement.common.controller.AbstractController;
import com.digigate.engineeringmanagement.configurationmanagement.dto.request.configuration.WorkShopRequestDto;
import com.digigate.engineeringmanagement.configurationmanagement.entity.WorkShop;
import com.digigate.engineeringmanagement.configurationmanagement.service.configuration.WorkShopService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/workshops")
public class WorkShopController extends AbstractController<WorkShop, WorkShopRequestDto> {
    public WorkShopController(WorkShopService service) {
        super(service);
    }
}
