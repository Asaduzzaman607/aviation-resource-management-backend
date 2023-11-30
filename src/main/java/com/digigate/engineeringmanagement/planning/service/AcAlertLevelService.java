package com.digigate.engineeringmanagement.planning.service;

import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.util.Helper;
import com.digigate.engineeringmanagement.planning.entity.AcAlertLevel;
import com.digigate.engineeringmanagement.planning.repository.AcAlertLevelRepository;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.json.XMLTokener.entity;

/**
 * Ac Alert Level service
 *
 * @author Nafiul Islam
 */
@Service
public class AcAlertLevelService  {

    private final AcAlertLevelRepository acAlertLevelRepository;

    public AcAlertLevelService(AcAlertLevelRepository acAlertLevelRepository) {
        this.acAlertLevelRepository = acAlertLevelRepository;
    }

    public void saveItem(List<AcAlertLevel> acAlertLevel){
        try{
             acAlertLevelRepository.saveAll(acAlertLevel);
        }catch (Exception e) {
            String name = entity.getClass().getSimpleName();
            throw EngineeringManagementServerException.dataSaveException(Helper.createDynamicCode(
                    ErrorId.DATA_NOT_SAVED_DYNAMIC,
                    name));
        }
    }
}
