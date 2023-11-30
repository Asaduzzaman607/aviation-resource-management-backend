package com.digigate.engineeringmanagement.configurationmanagement.service.administration;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.payload.response.CustomUserResponseDto;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.common.service.AbstractSearchService;
import com.digigate.engineeringmanagement.common.service.UserService;
import com.digigate.engineeringmanagement.common.specification.CustomSpecification;
import com.digigate.engineeringmanagement.configurationmanagement.dto.request.administration.ApprovalSettingDto;
import com.digigate.engineeringmanagement.configurationmanagement.dto.request.search.ApprovalSettingSearchDto;
import com.digigate.engineeringmanagement.configurationmanagement.dto.request.search.SelectedUserSearchDto;
import com.digigate.engineeringmanagement.configurationmanagement.dto.response.adminstration.ApprovalSettingCustomResponseDto;
import com.digigate.engineeringmanagement.configurationmanagement.dto.response.adminstration.ApprovalSettingResponseDto;
import com.digigate.engineeringmanagement.configurationmanagement.entity.ConfigSubmoduleItem;
import com.digigate.engineeringmanagement.configurationmanagement.entity.administration.ApprovalEmployee;
import com.digigate.engineeringmanagement.configurationmanagement.entity.administration.ApprovalSetting;
import com.digigate.engineeringmanagement.configurationmanagement.entity.administration.WorkFlowAction;
import com.digigate.engineeringmanagement.configurationmanagement.repository.administration.ApprovalSettingEntityMangerRepo;
import com.digigate.engineeringmanagement.configurationmanagement.repository.administration.ApprovalSettingRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.digigate.engineeringmanagement.common.constant.ApplicationConstant.*;

@Service
public class ApprovalSettingService extends AbstractSearchService<ApprovalSetting, ApprovalSettingDto, ApprovalSettingSearchDto> {
    private final UserService userService;
    private final ApprovalSettingRepository approvalSettingRepository;
    private final ApprovalFacadeService approvalFacadeService;
    private final WorkFlowActionService workflowActionService;
    private final ISubModuleItemService submoduleItemService;
    private final ApprovalSettingEntityMangerRepo approvalSettingEntityManger;

    @Autowired
    public ApprovalSettingService(UserService userService, ApprovalSettingRepository approvalSettingRepository,
                                  ApprovalFacadeService approvalFacadeService,
                                  @Lazy WorkFlowActionService workflowActionService, ISubModuleItemService submoduleItemService, ApprovalSettingEntityMangerRepo approvalSettingEntityManger) {
        super(approvalSettingRepository);
        this.userService = userService;
        this.approvalSettingRepository = approvalSettingRepository;
        this.approvalFacadeService = approvalFacadeService;
        this.workflowActionService = workflowActionService;
        this.submoduleItemService = submoduleItemService;
        this.approvalSettingEntityManger = approvalSettingEntityManger;
    }

    /*
     * saving data in database from ApprovalSettingDto
     * finding existing employees and not update if id already exists and otherwise insert
     * return approvalSetting object
     */
    @Override
    public ApprovalSetting create(ApprovalSettingDto approvalSettingDto) {
        Optional<ApprovalSetting> approvalSettingOptional = approvalSettingRepository
                .findByWorkFlowActionIdAndSubModuleItemIdAndIsActiveTrue(approvalSettingDto.getWorkFlowActionId(),
                        approvalSettingDto.getSubModuleItemId());
        ApprovalSetting approvalSetting = approvalSettingOptional.orElseGet(() -> populateAndSave(approvalSettingDto));

        Set<Long> allEmployeesId = approvalFacadeService
                .getAllEmployeeIdsByApprovalSettingId(approvalSetting.getId());
        Set<Long> filterEmployeeIds = allEmployeesId
                .stream()
                .filter(employeeId -> !approvalSettingDto.getEmployeeIds().contains(employeeId))
                .collect(Collectors.toSet());
        if (CollectionUtils.isNotEmpty(filterEmployeeIds)) {
            approvalFacadeService.deleteAllByApprovalSettingIdAndEmployeeIdIn(approvalSetting.getId(),filterEmployeeIds);
        }

        Set<Long> newEmployeeIds = approvalSettingDto.getEmployeeIds()
            .stream()
            .filter(id -> !allEmployeesId.contains(id))
            .collect(Collectors.toSet());

        if (newEmployeeIds.size() != userService.findUsernameByIdList(newEmployeeIds).size()) {
            throw EngineeringManagementServerException.badRequest(ErrorId.USER_NOT_EXISTS);
        }

        List<ApprovalEmployee> approvalEmployees = newEmployeeIds.stream()
                .map(id -> ApprovalEmployee.builder()
                     .employeeId(id)
                     .approvalSetting(approvalSetting)
                     .build())
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(approvalEmployees)) {
            approvalFacadeService.saveApprovalEmployee(approvalEmployees);
        }
        return approvalSetting;
    }

    @Override
    public void updateActiveStatus(Long id, Boolean isActive) {
        ApprovalSetting approvalSetting = findByIdUnfiltered(id);
        if (approvalSetting.getIsActive() == isActive) {
            throw EngineeringManagementServerException.badRequest(ErrorId.ONLY_TOGGLE_VALUE_ACCEPTED);
        }
        if (isActive == Boolean.TRUE) {
            Optional<ApprovalSetting> approvalSettingOptional = approvalSettingRepository
                .findByWorkFlowActionIdAndSubModuleItemIdAndIsActiveTrue(approvalSetting.getWorkFlowActionId(),
                    approvalSetting.getSubModuleItemId());
            if (approvalSettingOptional.isPresent()) {
                throw EngineeringManagementServerException.badRequest(ErrorId.APPROVAL_SETTINGS_ALREADY_EXISTS);
            }
        }

        if (isActive == Boolean.FALSE && approvalSetting.getIsActive() == Boolean.TRUE) {
            approvalSettingEntityManger.deleteAllPendingApprovals(approvalSetting);
        }
        approvalSetting.setIsActive(isActive);
        saveItem(approvalSetting);
    }

    @Override
    public PageData getAll(Boolean isActive, Pageable pageable) {
        Page<ApprovalSetting> pagedData = approvalSettingRepository.findAllByIsActive(isActive, pageable);
        Map<Long, WorkFlowAction> workFlowActionMap = workflowActionService.getSortedWorkflowActions(Sort.Direction.ASC)
                .stream()
                .collect(Collectors.toMap(WorkFlowAction::getId, Function.identity()));

        List<Object> models = pagedData
                .getContent()
                .stream()
                .map(response -> populateResponseDto(response, workFlowActionMap.get(response.getWorkFlowActionId()), null))
                .collect(Collectors.toList());

        return PageData.builder()
                .model(models)
                .totalPages(pagedData.getTotalPages())
                .totalElements(pagedData.getTotalElements())
                .currentPage(pageable.getPageNumber() + 1)
                .build();
    }

    public List<ApprovalSetting> findBySubmoduleId(Long subModuleItemId) {
        return approvalSettingRepository.findBySubModuleItemIdAndIsActiveTrue(subModuleItemId);
    }

    public ApprovalSettingCustomResponseDto getSelectedUsers(SelectedUserSearchDto searchDto) {
        Optional<ApprovalSetting> settingOptional = approvalSettingRepository
                .findByWorkFlowActionIdAndSubModuleItemIdAndIsActiveTrue(searchDto.getWorkFlowActionId(), searchDto.getSubModuleItemId());

        if (settingOptional.isEmpty()) {
            return ApprovalSettingCustomResponseDto.emptyResponse();
        }
        ApprovalSetting approvalSetting = settingOptional.get();

        List<CustomUserResponseDto> userDtos = approvalFacadeService.getAllUserByApprovalSettingId(approvalSetting.getId(),
                searchDto.getDepartmentId(), searchDto.getSectionId(), searchDto.getDesignationId());

        return convertToCustomResponseDto(userDtos, workflowActionService.findById(approvalSetting.getWorkFlowActionId()),
                approvalFacadeService.findSubmoduleItemById(approvalSetting.getSubModuleItemId()));
    }

    @Override
    public ApprovalSettingCustomResponseDto getSingle(Long id) {
        ApprovalSetting approvalSetting = findByIdUnfiltered(id);
        List<CustomUserResponseDto> employeeNamesMap = approvalFacadeService.getAllUserByApprovalSettingId(
                approvalSetting.getId(), null, null, null);

        return convertToCustomResponseDto(employeeNamesMap,
                workflowActionService.findById(approvalSetting.getWorkFlowActionId()),
                approvalFacadeService.findSubmoduleItemById(approvalSetting.getSubModuleItemId()));
    }

    @Override
    public PageData search(ApprovalSettingSearchDto searchDto, Pageable pageable) {
        Specification<ApprovalSetting> specification = buildSpecification(searchDto);
        Page<ApprovalSetting> pageData = approvalSettingRepository.findAll(specification, pageable);
        List<ApprovalSetting> approvalSettingList = pageData.getContent();
        Set<Long> submoduleItemIds = approvalSettingList.stream().map(ApprovalSetting::getSubModuleItemId).collect(Collectors.toSet());

        Set<Long> workFlowActionIds = approvalSettingList.stream().map(ApprovalSetting::getWorkFlowActionId).collect(Collectors.toSet());
        Map<Long, WorkFlowAction> workFlowActionMap = workflowActionService.getAllWorkFLowActionByIdIn(workFlowActionIds)
                .stream().collect(Collectors.toMap(WorkFlowAction::getId, Function.identity()));

        Map<Long, ConfigSubmoduleItem> submoduleItemMap = approvalFacadeService.getAllSubModuleItemsByIdIn
                        (submoduleItemIds).stream()
                .collect(Collectors.toMap(ConfigSubmoduleItem::getId, Function.identity()));

        List<Object> models = pageData.getContent().stream().map(response -> populateResponseDto(response,
                workFlowActionMap.get(response.getWorkFlowActionId()),
                submoduleItemMap.get(response.getSubModuleItemId()))).collect(Collectors.toList());

        return PageData.builder()
                .model(models)
                .totalPages(pageData.getTotalPages())
                .totalElements(pageData.getTotalElements())
                .currentPage(pageable.getPageNumber() + 1)
                .build();
    }

    private ApprovalSettingCustomResponseDto convertToCustomResponseDto(List<CustomUserResponseDto> selectedUser,
                                                                        WorkFlowAction workFlowAction,
                                                                        ConfigSubmoduleItem submoduleItem) {
        return ApprovalSettingCustomResponseDto.builder()
                .selectedUsers(selectedUser)
                .subModuleItemId(submoduleItem.getId())
                .subModuleItemName(submoduleItem.getItemName())
                .workFlowActionId(workFlowAction.getId())
                .workFlowActionName(workFlowAction.getName())
                .build();
    }

    private ApprovalSettingResponseDto populateResponseDto(ApprovalSetting response, WorkFlowAction workFlowAction, ConfigSubmoduleItem submoduleItem) {
        ApprovalSettingResponseDto approvalSettingResponseDto = convertToResponseDto(response);
        if (Objects.nonNull(workFlowAction)) {
            approvalSettingResponseDto.setWorkFlowActionName(workFlowAction.getName());
            approvalSettingResponseDto.setWorkFlowActionId(workFlowAction.getId());
        }
        if (Objects.nonNull(submoduleItem)) {
            approvalSettingResponseDto.setSubModuleItemName(submoduleItem.getItemName());
        }
        return approvalSettingResponseDto;
    }

    @Override
    protected ApprovalSetting convertToEntity(ApprovalSettingDto approvalSettingDto) {
        return null;
    }

    /*
     *updating ApprovalSetting data in database
     */
    @Override
    protected ApprovalSetting updateEntity(ApprovalSettingDto approvalSettingDto, ApprovalSetting approvalSetting) {
        approvalSetting.setWorkFlowAction(WorkFlowAction.withId(approvalSettingDto.getWorkFlowActionId()));
        approvalSetting.setSubModuleItemId(approvalSettingDto.getSubModuleItemId());
        return approvalSetting;
    }

    /*
     *convert the object ApprovalSetting to ApprovalSettingDto
     */
    @Override
    protected ApprovalSettingResponseDto convertToResponseDto(ApprovalSetting approvalSetting) {
        return ApprovalSettingResponseDto.builder()
                .id(approvalSetting.getId())
                .subModuleItemId(approvalSetting.getSubModuleItemId())
                .build();
    }

    /*
     *saving data approvalSettingDto to approvalSettingRepository
     */
    private ApprovalSetting populateAndSave(ApprovalSettingDto approvalSettingDto) {
        ConfigSubmoduleItem item = submoduleItemService.findById(approvalSettingDto.getSubModuleItemId());
        if (!workflowActionService.findById(approvalSettingDto.getWorkFlowActionId()).isShow()) {
            throw new EngineeringManagementServerException(ErrorId.WORK_FLOW_ACTION_NOT_ASSIGNABLE, HttpStatus.BAD_REQUEST, MDC.get(
                    ApplicationConstant.TRACE_ID));
        }
        return approvalSettingRepository.save(ApprovalSetting.builder()
                .workFlowAction(WorkFlowAction.withId(approvalSettingDto.getWorkFlowActionId()))
                .configSubmoduleItem(item)
                .build());
    }

    public Optional<ApprovalSetting> findByWorkFlowAndSubmoduleId(Long workFlowActionId, Long submoduleItemId) {
        return approvalSettingRepository
                .findByWorkFlowActionIdAndSubModuleItemIdAndIsActiveTrue(workFlowActionId, submoduleItemId);
    }

    @Override
    protected Specification<ApprovalSetting> buildSpecification(ApprovalSettingSearchDto searchDto) {
        CustomSpecification<ApprovalSetting> customSpecification = new CustomSpecification<>();
        return Specification.where(customSpecification.equalSpecificationAtRoot(searchDto.getIsActive(), IS_ACTIVE_FIELD)
                .and(customSpecification.likeSpecificationAtChild(searchDto.getWorkFlowActionName(), WORKFLOW_ACTION, ENTITY_NAME)
                        .and(customSpecification.likeSpecificationAtChild(searchDto.getSubModuleItemName(), CONFIG_SUBMODULE_ITEM, CONFIG_SUB_MODULE_ITEM_NAME))));

    }

    public void makeAllInactiveByWfa(Long workflowActionId) {
        List<ApprovalSetting> approvalSettingsList = approvalSettingRepository.findByWorkFlowActionIdAndIsActiveTrue(workflowActionId);
        approvalSettingsList.forEach(approvalSetting -> {
            approvalSettingEntityManger.deleteAllPendingApprovals(approvalSetting);
            approvalSetting.setIsActive(false);
        });
        saveItemList(approvalSettingsList);
    }
}
