package com.digigate.engineeringmanagement.common.service.impl;

import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.entity.AccessRight;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.repository.AccessRightRepository;
import com.digigate.engineeringmanagement.common.service.AccessRightService;
import com.digigate.engineeringmanagement.common.util.Helper;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.digigate.engineeringmanagement.common.constant.ApplicationConstant.FIRST_INDEX;

/**
 * Access Right Service Implementation
 *
 * @author Pranoy Das
 */
@Service
public class AccessRightServiceImpl implements AccessRightService {
    private final AccessRightRepository accessRightRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(AccessRightServiceImpl.class);

    /**
     * Autowired constructor
     *
     * @param accessRightRepository {@link AccessRightRepository}
     */
    @Autowired
    public AccessRightServiceImpl(AccessRightRepository accessRightRepository) {
        this.accessRightRepository = accessRightRepository;
    }

    /**
     * responsible for finding all access rights by access ids
     *
     * @param accessRightIds set of access right ids
     * @return access right
     */
    @Override
    public Set<AccessRight> findAllAccessRightsByIds(Set<Integer> accessRightIds) {
        return accessRightRepository.findAllByIdIn(accessRightIds);
    }

    @Override
    public void saveAccessRightList(List<AccessRight> accessRights) {

        try {
            if (CollectionUtils.isEmpty(accessRights)) {
                return;
            }
//            if (accessRightRepository.findById(accessRights.get(FIRST_INDEX).getId()).isPresent()) {
//                return;
//            }

            accessRightRepository.saveAll(accessRights);
        } catch (Exception e) {

            String entityName = accessRights.get(0).getClass().getSimpleName();
            LOGGER.error("Save failed for entity {}", entityName);
            LOGGER.error("Error message: {}", e.getMessage());
            throw EngineeringManagementServerException.dataSaveException(
                    Helper.createDynamicCode(ErrorId.DATA_NOT_SAVED_DYNAMIC, entityName)
            );
        }
    }
}
