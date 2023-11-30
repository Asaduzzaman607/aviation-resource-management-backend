package com.digigate.engineeringmanagement.configurationmanagement.service.administration;

import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.entity.Action;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.util.Helper;
import com.digigate.engineeringmanagement.configurationmanagement.repository.administration.ActionRepository;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

import static com.digigate.engineeringmanagement.common.constant.ApplicationConstant.FIRST_INDEX;

@Service
public class ActionService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ActionService.class);
    private final ActionRepository actionRepository;

    @Autowired
    public ActionService(ActionRepository actionRepository) {
        this.actionRepository = actionRepository;
    }


    public List<Action> saveAll(List<Action> actions) {
        try {
            if (CollectionUtils.isEmpty(actions)) {
                return actions;
            }
//            if (actionRepository.findById(actions.get(FIRST_INDEX).getId()).isPresent()) {
//                return Collections.emptyList();
//            }

            return actionRepository.saveAll(actions);
        } catch (Exception e) {
            String entityName = actions.get(0).getClass().getSimpleName();
            LOGGER.info("Save failed for entity {}", entityName);
            LOGGER.error("Error message: {}", e.getMessage());
            throw EngineeringManagementServerException
                    .dataSaveException(Helper.createDynamicCode(ErrorId.DATA_NOT_SAVED_DYNAMIC, entityName));
        }
    }
}
