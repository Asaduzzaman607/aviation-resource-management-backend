package com.digigate.engineeringmanagement.common.util;

import com.digigate.engineeringmanagement.common.constant.ApprovalStatusType;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.payload.request.search.WorkFlowDto;
import com.digigate.engineeringmanagement.common.service.UserService;
import com.digigate.engineeringmanagement.configurationmanagement.dto.projection.WorkFlowActionProjection;
import com.digigate.engineeringmanagement.configurationmanagement.entity.administration.ApprovalSetting;
import com.digigate.engineeringmanagement.configurationmanagement.entity.administration.WorkFlowAction;
import com.digigate.engineeringmanagement.configurationmanagement.service.administration.ApprovalEmployeeService;
import com.digigate.engineeringmanagement.configurationmanagement.service.administration.ApprovalSettingService;
import com.digigate.engineeringmanagement.configurationmanagement.service.administration.WorkFlowActionService;
import com.digigate.engineeringmanagement.storemanagement.entity.partsdemand.ApprovalStatus;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.UsernameProjection;
import com.digigate.engineeringmanagement.storemanagement.service.storedemand.ApprovalStatusService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.digigate.engineeringmanagement.common.constant.ApplicationConstant.INT_ONE;

@Component
public class WorkFlowUtil {

    private final ApprovalSettingService approvalSettingService;
    private final ApprovalStatusService approvalStatusService;
    private final ApprovalEmployeeService approvalEmployeeService;
    private final WorkFlowActionService workFlowActionService;
    private final UserService userService;
    private final Helper helper;

    public WorkFlowUtil(@Lazy ApprovalSettingService approvalSettingService,
                        @Lazy ApprovalStatusService approvalStatusService,
                        @Lazy ApprovalEmployeeService approvalEmployeeService,
                        @Lazy WorkFlowActionService workFlowActionService,
                        @Lazy UserService userService,
                        Helper helper) {
        this.approvalSettingService = approvalSettingService;
        this.approvalStatusService = approvalStatusService;
        this.approvalEmployeeService = approvalEmployeeService;
        this.workFlowActionService = workFlowActionService;
        this.userService = userService;
        this.helper = helper;
    }

    public boolean hasActionPerformPermission(Long submoduleItemId, Long workflowActionId) {
        Optional<ApprovalSetting> approvalSetting = approvalSettingService
                .findByWorkFlowAndSubmoduleId(workflowActionId, submoduleItemId);

        return approvalSetting.isPresent() && CollectionUtils.isNotEmpty(approvalEmployeeService
                .findAllExistingApprovalEmployees(approvalSetting.get().getId(),
                        Collections.singleton(Helper.getAuthUserId())));
    }

    /**
     * This Method is responsible for validate work flow
     *
     * @param subModuleItemId   Sub module item id
     * @param workflowActionIds list of submodule action id
     */
    public void validateWorkflow(Long subModuleItemId, List<Long> workflowActionIds) {
        if (workflowActionIds.stream().noneMatch(workFlowActionId ->
                hasActionPerformPermission(subModuleItemId, workFlowActionId))) {
            throw EngineeringManagementServerException.badRequest(ErrorId.ACCESS_DENIED);
        }
    }


    public WorkFlowAction revertAndFindPrevAction(WorkFlowAction action, ApprovalStatusType type, Long id) {
        WorkFlowAction prevAction = workFlowActionService.getNavigatedAction(false, action);
        WorkFlowAction initialAction = workFlowActionService.findInitialAction();

        if (!prevAction.equals(initialAction)) {
            approvalStatusService.deleteByParentAndWorkflow(type, id, prevAction.getId());
            return prevAction;
        }
        return action;
    }

    /**
     * This method is responsible for Getting name from approval status
     *
     * @param approvalStatuses set of approval status
     * @return approval status
     */
    public Map<Long, Pair<Pair<String, String>, WorkFlowAction>> getNamesFromApprovalStatuses(Set<ApprovalStatus> approvalStatuses) {
        Set<Long> userIds = new HashSet<>();
        Set<Long> actionIds = new HashSet<>();
        approvalStatuses.forEach(approvalStatus -> {
            userIds.add(approvalStatus.getUpdatedBy());
            actionIds.add(approvalStatus.getWorkFlowActionId());
        });

        Map<Long, Pair<String, String>> userMap = userService.findUsernameByIdList(userIds).stream().collect(
                Collectors.toMap(UsernameProjection::getId, usernameProjection ->
                        Pair.of(usernameProjection.getLogin(), usernameProjection.getEmployeeDesignationName()), (a, b) -> b));

        Map<Long, WorkFlowAction> workFlowActionMap = workFlowActionService
                .getAllByDomainIdInUnfiltered(actionIds)
                .stream()
                .collect(Collectors.toMap(WorkFlowAction::getId, Function.identity(), (a, b) -> b));

        return approvalStatuses.stream().collect(Collectors.toMap(AbstractDomainBasedEntity::getId, approvalStatus ->
                Pair.of(userMap.get(approvalStatus.getUpdatedBy()),
                        workFlowActionMap.get(approvalStatus.getWorkFlowActionId())), (a, b) -> b));
    }

    /**
     * This method is responsible for check validity for updating
     *
     * @param workFlowActionId work flow action id
     */
    public void validateUpdatability(Long workFlowActionId) {
        if (Objects.equals(workFlowActionId, workFlowActionService.findFinalAction().getId())) {
            throw EngineeringManagementServerException.badRequest(ErrorId.ALREADY_APPROVED);
        }
    }

    /**
     * This method is responsible for getting the set of work flow ids
     *
     * @return set of pending workflow
     * @param approvedActionForUser
     */
    public Set<Long> findPendingWorkFlowIds(List<WorkFlowActionProjection> approvedActionForUser) {
        approvedActionForUser.addAll(approvalEmployeeService
                .findApprovedActionsForUser(helper.getSubModuleItemId(), Helper.getAuthUserId()));
        Optional<WorkFlowActionProjection> minApprovedActionOrderForUser = approvedActionForUser.stream().findFirst();
        if (minApprovedActionOrderForUser.isEmpty()) {
            return null;
        }
        Integer minApprovedOrder = minApprovedActionOrderForUser.get().getOrderNumber();
        return workFlowActionService.findByPendingWorkFlowIds(minApprovedOrder);
    }

    /**
     * Responsible for preparing data
     *
     * @param approvedActions {@link WorkFlowActionProjection}
     * @return prepared data
     */
    public WorkFlowDto prepareResponseData(Set<Long> ids,
                                           List<WorkFlowActionProjection> approvedActions,
                                           ApprovalStatusType approvalStatusType) {
        WorkFlowDto workFlowDto = new WorkFlowDto();

        Set<ApprovalStatus> approvalStatuses = approvalStatusService.findByParent(approvalStatusType, ids);

        workFlowDto.setStatusMap(approvalStatuses.stream().collect(Collectors.groupingBy(ApprovalStatus::getParentId)));

        workFlowDto.setNamesFromApprovalStatuses(getNamesFromApprovalStatuses(approvalStatuses));

        List<WorkFlowAction> sortedWorkflowActions = workFlowActionService.getSortedWorkflowActions(Sort.Direction.ASC);

        workFlowDto.setWorkFlowActionMap(sortedWorkflowActions.stream().collect(
                Collectors.toMap(WorkFlowAction::getId, Function.identity(), (a, b) -> b)));

        List<Long> sortedKeys = sortedWorkflowActions.stream().map(WorkFlowAction::getId).collect(Collectors.toList());

        approvedActions.forEach(workFlowActionProjection -> {
                    Long actionId = workFlowActionProjection.getActionId();
                    workFlowDto.getActionableIds().add(actionId);
                    workFlowDto.getEditableIds().add(actionId);
                    workFlowDto.getEditableIds().add(workFlowActionService
                            .getEditableActionsByIndex(sortedKeys.indexOf(actionId) + INT_ONE,
                                    sortedWorkflowActions).getId());
                }
        );
        return workFlowDto;
    }
}
