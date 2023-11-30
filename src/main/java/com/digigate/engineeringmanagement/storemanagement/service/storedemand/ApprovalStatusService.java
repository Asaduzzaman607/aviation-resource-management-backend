package com.digigate.engineeringmanagement.storemanagement.service.storedemand;

import com.digigate.engineeringmanagement.common.constant.ApprovalStatusType;
import com.digigate.engineeringmanagement.common.service.AbstractService;
import com.digigate.engineeringmanagement.common.util.Helper;
import com.digigate.engineeringmanagement.configurationmanagement.entity.administration.WorkFlowAction;
import com.digigate.engineeringmanagement.configurationmanagement.service.administration.WorkFlowActionService;
import com.digigate.engineeringmanagement.storemanagement.entity.partsdemand.ApprovalStatus;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand.ApprovalStatusDto;
import com.digigate.engineeringmanagement.storemanagement.payload.response.storedemand.ApprovalStatusViewModel;
import com.digigate.engineeringmanagement.storemanagement.repository.storedemand.ApprovalStatusRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class ApprovalStatusService extends AbstractService<ApprovalStatus, ApprovalStatusDto> {
    private final WorkFlowActionService workFlowActionService;
    private final ApprovalStatusRepository approvalStatusRepository;

    public ApprovalStatusService(ApprovalStatusRepository approvalStatusRepository, WorkFlowActionService workFlowActionService) {
        super(approvalStatusRepository);
        this.workFlowActionService = workFlowActionService;
        this.approvalStatusRepository = approvalStatusRepository;
    }

    public void deleteAllByParentIdAndApprovalStatusType(Long parentId, List<ApprovalStatusType> approvalStatusType){
        approvalStatusRepository.deleteAllByParentIdAndApprovalStatusTypeIn(parentId, approvalStatusType);
    }

    public String findUserNameByWorkflowIdAndStatusType(Long workFlowActionId, Long parentId,
                                                        ApprovalStatusType approvalStatusType) {
        return approvalStatusRepository.findUserNameByWorkflowIdAndStatusType(workFlowActionId, parentId, approvalStatusType);
    }

    public void createApprovalStatusForManualUser(ApprovalStatusDto approvalStatusDto, Long updatedById){
        ApprovalStatus approvalStatus = new ApprovalStatus();
        BeanUtils.copyProperties(approvalStatusDto, approvalStatus);
        approvalStatus.setUpdatedBy(updatedById);
        saveItem(approvalStatus);
    }

    @Override
    protected ApprovalStatusViewModel convertToResponseDto(ApprovalStatus approvalStatus) {
        return null;
    }

    @Override
    protected ApprovalStatus convertToEntity(ApprovalStatusDto approvalStatusDto) {
        ApprovalStatus approvalStatus = new ApprovalStatus();
        BeanUtils.copyProperties(approvalStatusDto, approvalStatus);
        approvalStatus.setUpdatedBy(Helper.getAuthUserId());
        return approvalStatus;
    }

    @Override
    protected ApprovalStatus updateEntity(ApprovalStatusDto dto, ApprovalStatus entity) {
        WorkFlowAction workFlowAction = workFlowActionService.findById(entity.getWorkFlowActionId());
        entity.setParentId(dto.getParentId());
        entity.setApprovalStatusType(dto.getApprovalStatusType());
        entity.setWorkFlowAction(workFlowAction);
        return entity;
    }

    public Set<ApprovalStatus> findByParent(ApprovalStatusType statusType, Set<Long> parentIds) {
        return approvalStatusRepository.findByApprovalStatusTypeAndParentIdInAndIsActiveTrue(statusType, parentIds);
    }

    public void deleteByParentAndWorkflow(ApprovalStatusType type, Long parentId, Long workFlowActionId) {
        approvalStatusRepository.findByApprovalStatusTypeAndParentIdAndWorkFlowActionIdAndIsActiveTrue(type, parentId,
                workFlowActionId).forEach(approvalStatus -> updateActiveStatus(approvalStatus.getId(), false));
    }

    public void deleteByParentAndApprovalType(ApprovalStatusType type, Long parentId, Long initialActionId) {
        approvalStatusRepository.findByApprovalStatusTypeAndParentIdAndWorkFlowActionIdNotAndIsActiveTrue(type, parentId, initialActionId).forEach(approvalStatus ->
            updateActiveStatus(approvalStatus.getId(), false));
    }

    public WorkFlowAction resetAction(ApprovalStatusType type, Long parentId) {
        WorkFlowAction initialAction = workFlowActionService.findInitialAction();
        deleteByParentAndApprovalType(type, parentId, initialAction.getId());
        return workFlowActionService.getNavigatedAction(true, initialAction);
    }
}
