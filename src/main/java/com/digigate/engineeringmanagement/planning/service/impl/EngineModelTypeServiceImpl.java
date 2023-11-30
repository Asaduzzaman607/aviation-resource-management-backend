package com.digigate.engineeringmanagement.planning.service.impl;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.service.impl.RoleServiceImpl;
import com.digigate.engineeringmanagement.planning.converter.EngineModelTypeConverter;
import com.digigate.engineeringmanagement.planning.entity.EngineModelType;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.planning.payload.request.EngineModelTypeDto;
import com.digigate.engineeringmanagement.planning.repository.EngineModelTypeRepository;
import com.digigate.engineeringmanagement.planning.service.EngineModelTypeService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class EngineModelTypeServiceImpl implements EngineModelTypeService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RoleServiceImpl.class);
    private final EngineModelTypeRepository typeRepository;

    public EngineModelTypeServiceImpl(EngineModelTypeRepository typeRepository) {
        this.typeRepository = typeRepository;
    }

    @Override
    public EngineModelType findById(Integer id) {
        if (Objects.isNull(id)) {
            throw new EngineeringManagementServerException(
                    ErrorId.ENGINE_MODEL_TYPE_ID_IS_REQUIRED,
                    HttpStatus.BAD_REQUEST,
                    MDC.get(ApplicationConstant.TRACE_ID));
        }

        return typeRepository.findById(id).orElseThrow(() -> {
            throw new EngineeringManagementServerException(
                    ErrorId.ENGINE_MODEL_TYPE_NOT_EXISTS,
                    HttpStatus.NOT_FOUND,
                    MDC.get(ApplicationConstant.TRACE_ID));
        });
    }

    @Override
    public Integer save(EngineModelTypeDto engineModelTypeDto) {
        if (StringUtils.isBlank(engineModelTypeDto.getName())) {
            LOGGER.error("Type name is required.");
            throw new EngineeringManagementServerException(
                    ErrorId.ENGINE_MODEL_TYPE_NAME_MUST_NOT_BE_EMPTY,
                    HttpStatus.BAD_REQUEST,
                    MDC.get(ApplicationConstant.TRACE_ID)
            );
        }

        if (typeRepository.existsByName(engineModelTypeDto.getName())) {
            LOGGER.error("Type name already exists");
            throw new EngineeringManagementServerException(
                    ErrorId.ENGINE_MODEL_TYPE_NAME_ALREADY_EXISTS,
                    HttpStatus.BAD_REQUEST,
                    MDC.get(ApplicationConstant.TRACE_ID)
            );
        }

        try {
            return typeRepository.save(EngineModelTypeConverter.dtoToEntity(engineModelTypeDto, new EngineModelType())).getId();
        } catch (Exception e) {
            LOGGER.error("Engine model type is not saved : {}", engineModelTypeDto);
            throw new EngineeringManagementServerException(
                    ErrorId.ENGINE_MODEL_TYPE_IS_NOT_SAVED,
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    MDC.get(ApplicationConstant.TRACE_ID)
            );
        }
    }

    /**
     * This is method is responsible for updating role
     *
     * @param engineModelTypeDto {@link  EngineModelTypeDto}
     * @return newly updated role id        {@link Integer}
     */
    @Override
    public Integer update(EngineModelTypeDto engineModelTypeDto) {

        if (StringUtils.isBlank(engineModelTypeDto.getName())) {
            LOGGER.error("Type name is required.");
            throw new EngineeringManagementServerException(
                    ErrorId.ROLE_NAME_MUST_NOT_BE_EMPTY,
                    HttpStatus.BAD_REQUEST,
                    MDC.get(ApplicationConstant.TRACE_ID)
            );
        }

        if (typeRepository.existsByNameAndIdIsNot(engineModelTypeDto.getName(), engineModelTypeDto.getId())) {
            LOGGER.error("Type name already exists");
            throw new EngineeringManagementServerException(
                    ErrorId.ENGINE_MODEL_TYPE_NAME_ALREADY_EXISTS,
                    HttpStatus.BAD_REQUEST,
                    MDC.get(ApplicationConstant.TRACE_ID)
            );
        }

        try {
            return typeRepository.save(EngineModelTypeConverter.dtoToEntity(engineModelTypeDto, new EngineModelType())).getId();

        } catch (Exception e) {
            LOGGER.error("Engine model type is not saved : {}", engineModelTypeDto);
            throw new EngineeringManagementServerException(
                    ErrorId.ENGINE_MODEL_TYPE_IS_NOT_UPDATED,
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    MDC.get(ApplicationConstant.TRACE_ID)
            );
        }

    }

    @Override
    public Integer toggleStatus(Integer id) {
        EngineModelType modelType = findById(id);
        modelType.setIsActive(!modelType.getIsActive());

        try {
            typeRepository.save(modelType);
            return modelType.getId();
        } catch (Exception e) {
            LOGGER.error("Could not change engine type status!");
            throw new EngineeringManagementServerException(
                    ErrorId.ENGINE_MODEL_TYPE_STATUS_CHANGED_FAILED,
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    MDC.get(ApplicationConstant.TRACE_ID)
            );
        }
    }

    @Override
    public List<EngineModelType> list() {
        return typeRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }

    @Override
    public EngineModelType findActiveEngineModelType(Integer id) {
        return typeRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() ->
                        new EngineeringManagementServerException(
                                ErrorId.ENGINE_MODEL_TYPE_NOT_EXISTS,
                                HttpStatus.NOT_FOUND,
                                MDC.get(ApplicationConstant.TRACE_ID))
                );
    }
}
