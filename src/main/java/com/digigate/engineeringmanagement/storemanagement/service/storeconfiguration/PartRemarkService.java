package com.digigate.engineeringmanagement.storemanagement.service.storeconfiguration;

import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.payload.response.ApprovalRemarksResponseDto;
import com.digigate.engineeringmanagement.common.service.impl.RoleServiceImpl;
import com.digigate.engineeringmanagement.common.util.Helper;
import com.digigate.engineeringmanagement.configurationmanagement.entity.administration.WorkFlowAction;
import com.digigate.engineeringmanagement.configurationmanagement.service.administration.WorkFlowActionService;
import com.digigate.engineeringmanagement.storemanagement.constant.RemarkType;
import com.digigate.engineeringmanagement.storemanagement.entity.partsdemand.ApprovalStatus;
import com.digigate.engineeringmanagement.storemanagement.entity.storeconfiguration.PartRemark;
import com.digigate.engineeringmanagement.storemanagement.repository.storeconfiguration.PartRemarkRepository;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PartRemarkService {
    private final PartRemarkRepository partRemarkRepository;
    private final WorkFlowActionService workFlowActionService;

    protected static final Logger LOGGER = LoggerFactory.getLogger(RoleServiceImpl.class);

    @Autowired
    public PartRemarkService(PartRemarkRepository partRemarkRepository, WorkFlowActionService workFlowActionService) {
        this.partRemarkRepository = partRemarkRepository;
        this.workFlowActionService = workFlowActionService;
    }

    public Set<PartRemark> findByItemIdInAndRemarkTypeAndParentIdIn(Set<Long> ids, RemarkType remarkType, Set<Long> parentIds) {
        return partRemarkRepository.findByItemIdInAndRemarkTypeAndParentIdIn(ids, remarkType, parentIds);
    }

    public List<PartRemark> findByParentIdAndRemarkType(Set<Long> ids, RemarkType remarkType) {
        return partRemarkRepository.findByParentIdInAndRemarkType(ids, remarkType);
    }

    public void deleteByItemIdInAndParentIdAndRemarkType(Set<Long> ids, Long parentId, RemarkType remarkType) {
        partRemarkRepository.deleteByItemIdInAndParentIdAndRemarkType(ids, parentId, remarkType);
    }

    public void deleteByParentIdAndRemarkType(Long parentId, RemarkType remarkType) {
        partRemarkRepository.deleteByParentIdAndRemarkType(parentId, remarkType);
    }
    @Transactional
    public void deleteByParentIdAndRemarkTypeIn(Long parentId, List<RemarkType> remarkType) {
        partRemarkRepository.deleteByParentIdAndRemarkTypeIn(parentId, remarkType);
    }

    public void saveAll(List<PartRemark> partRemarks) {

        try {
            partRemarkRepository.saveAll(partRemarks);
        } catch (Exception e) {
            String entityName = partRemarks.get(0).getClass().getSimpleName();
            LOGGER.error("Save failed for entity {}", entityName);
            LOGGER.error("Error message: {}", e.getMessage());
            throw EngineeringManagementServerException.dataSaveException(Helper.createDynamicCode(ErrorId.DATA_NOT_SAVED_DYNAMIC,
                    entityName));
        }
    }

    public PartRemark findByItemIdAndRemarkTypeAndParentId(Long itemId, RemarkType remarkType, Long parentId) {
        return partRemarkRepository.findByItemIdAndRemarkTypeAndParentId(itemId, remarkType, parentId)
            .orElseGet(() -> buildRemarks(parentId, remarkType, itemId, null));
    }

    public void save(PartRemark partRemark) {
        partRemarkRepository.save(partRemark);
    }

    public void saveOrUpdateRemarks(Map<Long, String> map, Long parentId, RemarkType remarkType) {
        deleteByItemIdInAndParentIdAndRemarkType(map.keySet(), parentId, remarkType);
        List<PartRemark> partRemarkList = map.keySet().stream().filter(key -> StringUtils.isNotEmpty(map.get(key)))
                .map(key -> buildRemarks(parentId, remarkType, key, map.get(key)))
                .collect(Collectors.toList());
        saveAll(partRemarkList);
    }

    private PartRemark buildRemarks(Long parentId, RemarkType remarkType, Long itemId, String remarkString) {
        PartRemark partRemark = new PartRemark();
        partRemark.setRemarkType(remarkType);
        partRemark.setRemark(remarkString);
        partRemark.setParentId(parentId);
        partRemark.setItemId(itemId);
        return partRemark;
    }

    public void saveApproveRemark(Long parentId,Long workFlowActionId, RemarkType remarkType,String remark){
        PartRemark partRemark = new PartRemark();
        partRemark.setRemarkType(remarkType);
        partRemark.setRemark(remark);
        partRemark.setParentId(parentId);
        partRemark.setItemId(workFlowActionId);
        save(partRemark);
    }

    public ApprovalRemarksResponseDto prepareApprovalRemarkResponse(PartRemark partRemark, Map<Long, ApprovalStatus> workFlowActionMap, Map<Long, Pair<Pair<String, String>, WorkFlowAction>> namesFromApprovalStatuses) {
        ApprovalStatus approvalStatus = workFlowActionMap.get(partRemark.getItemId());
        Pair<Pair<String, String>, WorkFlowAction> pair = namesFromApprovalStatuses.get(approvalStatus.getId());
        return ApprovalRemarksResponseDto.builder()
                .workFlowActionName(approvalStatus.getWorkFlowAction().getName())
                .approvedBy(pair.getKey().getKey())
                .approvalDate(approvalStatus.getCreatedAt().toLocalDate())
                .approvalRemark(partRemark.getRemark())
                .build();
    }

    @Transactional
    public void revertPreviousActionRemarks(Long parentId, WorkFlowAction workFlowAction, RemarkType remarkType) {
        WorkFlowAction initialAction = workFlowActionService.findInitialAction();
        if (!workFlowAction.equals(initialAction)) {
            deleteByItemIdInAndParentIdAndRemarkType(Set.of(workFlowAction.getId()), parentId, remarkType);
        }
    }
}
