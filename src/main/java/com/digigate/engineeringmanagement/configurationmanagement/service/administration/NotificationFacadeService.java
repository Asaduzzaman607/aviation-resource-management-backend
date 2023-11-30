package com.digigate.engineeringmanagement.configurationmanagement.service.administration;

import com.digigate.engineeringmanagement.configurationmanagement.entity.administration.NotificationEmployee;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This class used as a bridge between NotificationSetting and NotificationEmployee services
 */
@Service
public class NotificationFacadeService {
    private final NotificationEmployeeService notificationEmployeeService;

    public NotificationFacadeService(NotificationEmployeeService notificationEmployeeService) {
        this.notificationEmployeeService = notificationEmployeeService;
    }

    public void saveNotificationEmployee(List<NotificationEmployee> notificationEmployees) {
        notificationEmployeeService.saveAll(notificationEmployees);
    }

    /**
     * This Method is for getting all the existing Employees with corresponding notificationSetting id
     *
     * @param notificationSettingId
     * @param employeeIds           return Sets of employees id with corresponding notificationSettingId
     */
    public Set<Long> findAllExistingNotificationEmployees(Long notificationSettingId, Set<Long> employeeIds) {
        return notificationEmployeeService.findAllExistingNotificationEmployees(notificationSettingId, employeeIds).stream()
                .map(NotificationEmployee::getEmployeeId)
                .collect(Collectors.toSet());
    }
}