package com.digigate.engineeringmanagement.storemanagement.repository.storedemand;

import com.digigate.engineeringmanagement.common.constant.ApprovalStatusType;
import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.storemanagement.entity.partsdemand.ApprovalStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ApprovalStatusRepository extends AbstractRepository<ApprovalStatus> {
    Set<ApprovalStatus> findByApprovalStatusTypeAndParentIdInAndIsActiveTrue(ApprovalStatusType statusType, Set<Long> parentIds);

    Set<ApprovalStatus> findByApprovalStatusTypeAndParentIdAndWorkFlowActionIdAndIsActiveTrue(ApprovalStatusType type, Long parentId,
                                                                                              Long workFlowActionId);

    Iterable<ApprovalStatus> findByApprovalStatusTypeAndParentIdAndWorkFlowActionIdNotAndIsActiveTrue(ApprovalStatusType type, Long parentId, Long initialActionId);

    void deleteAllByParentIdAndApprovalStatusTypeIn(Long parentId, List<ApprovalStatusType> approvalStatusType);

   @Query(value = "select u.login " +
            "from ApprovalStatus aps join User u on u.id = aps.updatedBy " +
            "where aps.workFlowActionId = :workFlowActionId " +
            "and aps.parentId = :parentId " +
            "and aps.approvalStatusType = :approvalStatusType"
    )
    String findUserNameByWorkflowIdAndStatusType(@Param("workFlowActionId") Long workFlowActionId,
                                               @Param("parentId") Long parentId,
                                               @Param("approvalStatusType") ApprovalStatusType approvalStatusType);

}
