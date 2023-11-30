package com.digigate.engineeringmanagement.configurationmanagement.service.administration;

import com.digigate.engineeringmanagement.common.service.AbstractService;
import com.digigate.engineeringmanagement.configurationmanagement.dto.response.adminstration.NotificationEmployeeResponseDto;
import com.digigate.engineeringmanagement.configurationmanagement.dto.response.adminstration.NotificationSettingResponseDto;
import com.digigate.engineeringmanagement.configurationmanagement.entity.administration.NotificationEmployee;
import com.digigate.engineeringmanagement.configurationmanagement.repository.administration.NotificationEmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class NotificationEmployeeService extends AbstractService<NotificationEmployee, NotificationEmployee> {
    private final NotificationEmployeeRepository repository;

    public NotificationEmployeeService(NotificationEmployeeRepository repository) {
        super(repository);
        this.repository = repository;
    }

    public void saveAll(List<NotificationEmployee> employees) {
        repository.saveAll(employees);
    }

    @Override
    protected NotificationEmployeeResponseDto convertToResponseDto(NotificationEmployee notificationEmployee) {
        return NotificationEmployeeResponseDto.builder()
                .id(notificationEmployee.getId())
                .employeeId(notificationEmployee.getEmployeeId())
                .notificationSetting(NotificationSettingResponseDto.builder()
                        .id(notificationEmployee.getNotificationSetting().getId())
                        .submoduleItemId(notificationEmployee.getNotificationSetting().getSubmoduleItemId())
                        .workflowActionName(notificationEmployee.getNotificationSetting().getWorkFlowAction().getName())
                        .build()).build();
    }

    @Override
    protected NotificationEmployee convertToEntity(NotificationEmployee notificationEmployee) {
        return notificationEmployee;
    }

    @Override
    protected NotificationEmployee updateEntity(NotificationEmployee dto, NotificationEmployee entity) {
        return entity;
    }

    public Set<NotificationEmployee> findAllExistingNotificationEmployees(Long id, Set<Long> employeeIds) {
        return repository.findAllByNotificationSettingIdAndEmployeeIdInAndIsActiveTrue(id, employeeIds);
    }
}
