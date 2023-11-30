package com.digigate.engineeringmanagement.configurationmanagement.repository.administration;

import com.digigate.engineeringmanagement.common.loader.SubModuleJsonLoader;
import com.digigate.engineeringmanagement.configurationmanagement.entity.administration.ApprovalSetting;
import com.digigate.engineeringmanagement.configurationmanagement.entity.administration.WorkFlowAction;
import com.digigate.engineeringmanagement.configurationmanagement.service.administration.WorkFlowActionService;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;

@Repository
public class ApprovalSettingEntityMangerRepo {
    private final EntityManager entityManager;
    private final WorkFlowActionService workFlowActionService;

    protected ApprovalSettingEntityMangerRepo(EntityManager entityManager, WorkFlowActionService workFlowActionService) {
        this.entityManager = entityManager;
        this.workFlowActionService = workFlowActionService;
    }

    public void deleteAllPendingApprovals(ApprovalSetting approvalSetting) {
        Long subModuleItemId = approvalSetting.getSubModuleItemId();
        Pair<String, Boolean> pair = SubModuleJsonLoader.getTableSmiMap().get(subModuleItemId);
        String tableName = pair.getLeft();
        WorkFlowAction navigatedAction = workFlowActionService.getNavigatedAction(true, approvalSetting.getWorkFlowAction(),
                subModuleItemId);
        String q = "update " + tableName + " set workflow_action_id = " + navigatedAction.getId() + " where " +
            " workflow_action_id = " + approvalSetting.getWorkFlowActionId();
        if (pair.getRight() == Boolean.TRUE) {
            q += " AND submodule_item_id = " + approvalSetting.getSubModuleItemId();
        }
        Query nativeQuery = entityManager.createNativeQuery(q);
        nativeQuery.executeUpdate();
    }
}
