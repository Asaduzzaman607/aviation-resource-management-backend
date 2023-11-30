package com.digigate.engineeringmanagement.configurationmanagement.repository.administration;

import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.configurationmanagement.entity.administration.ApprovalSetting;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApprovalSettingRepository extends AbstractRepository<ApprovalSetting> {

    Optional<ApprovalSetting> findByWorkFlowActionIdAndSubModuleItemIdAndIsActiveTrue(Long workFlowAction, Long SubModuleId);

    List<ApprovalSetting> findBySubModuleItemIdAndIsActiveTrue(Long subModuleItemId);

    List<ApprovalSetting> findByWorkFlowActionIdAndIsActiveTrue(Long workflowActionId);
}
