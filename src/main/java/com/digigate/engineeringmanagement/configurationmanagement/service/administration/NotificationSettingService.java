package com.digigate.engineeringmanagement.configurationmanagement.service.administration;

import com.digigate.engineeringmanagement.common.service.AbstractService;
import com.digigate.engineeringmanagement.configurationmanagement.dto.request.administration.NotificationSettingRequestDto;
import com.digigate.engineeringmanagement.configurationmanagement.dto.response.adminstration.NotificationSettingResponseDto;
import com.digigate.engineeringmanagement.configurationmanagement.entity.administration.NotificationEmployee;
import com.digigate.engineeringmanagement.configurationmanagement.entity.administration.NotificationSetting;
import com.digigate.engineeringmanagement.configurationmanagement.entity.administration.WorkFlowAction;
import com.digigate.engineeringmanagement.configurationmanagement.repository.administration.NotificationSettingRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class NotificationSettingService extends AbstractService<NotificationSetting, NotificationSettingRequestDto> {
    private final NotificationSettingRepository notificationSettingRepository;
    private final NotificationFacadeService facadeService;
    private final WorkFlowActionService workFlowActionService;

    public NotificationSettingService(NotificationSettingRepository notificationSettingRepository,
                                      NotificationFacadeService facadeService, WorkFlowActionService workFlowActionService) {
        super(notificationSettingRepository);
        this.notificationSettingRepository = notificationSettingRepository;
        this.facadeService = facadeService;
        this.workFlowActionService = workFlowActionService;
    }

    /**
     * Converting NotificationSetting Entity to Object
     * <p>
     * return NotificationSettingResponseDto
     */
    @Override
    protected NotificationSettingResponseDto convertToResponseDto(NotificationSetting notificationSetting) {
        return NotificationSettingResponseDto.builder()
                .id(notificationSetting.getId())
                .workflowActionName(notificationSetting.getWorkFlowAction().getName())
                .submoduleItemId(notificationSetting.getSubmoduleItemId())
                .build();
    }

    @Override
    public NotificationSetting create(NotificationSettingRequestDto requestDto) {
        /*
         * fetching value from database with existing SubmoduleItem and Workflow
         */
        Optional<NotificationSetting> settingOptional = notificationSettingRepository.findBySubmoduleItemIdAndWorkFlowActionIdAndIsActiveTrue(
                requestDto.getSubmoduleItemId(), requestDto.getWorkflowActionId());

        workFlowActionService.findById(requestDto.getWorkflowActionId());
        /*
         * Populating database if settingOptional is empty
         */
        NotificationSetting notificationSetting = settingOptional.orElseGet(() -> populateAndSave(requestDto));
        /*
         * fetching existing employee id with notification id
         */
        Set<Long> employees = facadeService.findAllExistingNotificationEmployees(notificationSetting.getId(), requestDto.getEmployeeIds());
        /*
         * filtering and mapping those fetched employees id to list of NotificationEmployee
         */
        List<NotificationEmployee> notificationEmployees = requestDto
                .getEmployeeIds()
                .stream()
                .filter(id -> !employees.contains(id))
                .map(id -> populateNotificationEmployee(notificationSetting, id))
                .collect(Collectors.toList());
        /*
         * saving those new assign employee in NotificationEmp;loyee
         */
        if (CollectionUtils.isNotEmpty(notificationEmployees)) {
            facadeService.saveNotificationEmployee(notificationEmployees);
        }
        return notificationSetting;
    }

    @Override
    protected NotificationSetting convertToEntity(NotificationSettingRequestDto requestDto) {
        return null;
    }

    private NotificationEmployee populateNotificationEmployee(NotificationSetting notificationSetting, Long id) {
        return NotificationEmployee
                .builder()
                .employeeId(id)
                .notificationSetting(notificationSetting)
                .build();
    }

    private NotificationSetting populateAndSave(NotificationSettingRequestDto requestDto) {
        NotificationSetting setting = NotificationSetting.builder()
                .submoduleItemId(requestDto.getSubmoduleItemId())
                .workFlowAction(WorkFlowAction.withId(requestDto.getWorkflowActionId()))
                .build();
        return notificationSettingRepository.save(setting);
    }

    @Override
    protected NotificationSetting updateEntity(NotificationSettingRequestDto dto, NotificationSetting entity) {
        workFlowActionService.findById(dto.getWorkflowActionId());
        entity.setSubmoduleItemId(dto.getSubmoduleItemId());
        entity.setWorkFlowAction(WorkFlowAction.withId(dto.getWorkflowActionId()));
        return entity;
    }
}
