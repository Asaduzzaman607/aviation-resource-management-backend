package com.digigate.engineeringmanagement.configurationmanagement.repository.administration;

import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.configurationmanagement.entity.administration.NotificationSetting;
import com.digigate.engineeringmanagement.configurationmanagement.entity.administration.WorkFlowAction;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotificationSettingRepository extends AbstractRepository<NotificationSetting> {
    Optional<NotificationSetting> findBySubmoduleItemIdAndWorkFlowActionIdAndIsActiveTrue(Long submoduleId, Long workflowActionId);
}
