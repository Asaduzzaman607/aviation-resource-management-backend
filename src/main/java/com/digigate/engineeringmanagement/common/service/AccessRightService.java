package com.digigate.engineeringmanagement.common.service;

import com.digigate.engineeringmanagement.common.entity.AccessRight;

import java.util.List;
import java.util.Set;

/**
 * access right service
 *
 * @author Pranoy Das
 */
public interface AccessRightService {
    Set<AccessRight> findAllAccessRightsByIds(Set<Integer> accessRightIds);
    void saveAccessRightList(List<AccessRight> accessRights);
}
