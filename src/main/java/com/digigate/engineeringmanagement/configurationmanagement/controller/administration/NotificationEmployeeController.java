package com.digigate.engineeringmanagement.configurationmanagement.controller.administration;

import com.digigate.engineeringmanagement.common.controller.AbstractController;
import com.digigate.engineeringmanagement.configurationmanagement.entity.administration.NotificationEmployee;
import com.digigate.engineeringmanagement.configurationmanagement.service.administration.NotificationEmployeeService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notification-employee")
public class NotificationEmployeeController extends AbstractController<NotificationEmployee, NotificationEmployee> {
    public NotificationEmployeeController(NotificationEmployeeService service) {
        super(service);
    }
}
