package com.digigate.engineeringmanagement.configurationmanagement.repository.administration;

import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.configurationmanagement.entity.administration.NotificationEmployee;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface NotificationEmployeeRepository extends AbstractRepository<NotificationEmployee> {
    Set<NotificationEmployee> findAllByNotificationSettingIdAndEmployeeIdInAndIsActiveTrue(Long notificationSettingsId,
                                                                                             Set<Long> employeeIds);
}
