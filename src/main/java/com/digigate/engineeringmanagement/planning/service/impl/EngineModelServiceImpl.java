package com.digigate.engineeringmanagement.planning.service.impl;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.common.service.AbstractSearchService;
import com.digigate.engineeringmanagement.common.specification.CustomSpecification;
import com.digigate.engineeringmanagement.configurationmanagement.entity.Aircraft;
import com.digigate.engineeringmanagement.configurationmanagement.service.aircraftinformation.AircraftService;
import com.digigate.engineeringmanagement.planning.entity.EngineModel;
import com.digigate.engineeringmanagement.planning.entity.EngineModelType;
import com.digigate.engineeringmanagement.planning.payload.request.EngineModelDto;
import com.digigate.engineeringmanagement.planning.payload.request.EngineModelSearchDto;
import com.digigate.engineeringmanagement.planning.payload.response.EngineModelViewPayload;
import com.digigate.engineeringmanagement.planning.service.EngineModelTypeService;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Engine model logic implementation
 *
 * @author Pranoy Das
 */
@Service
public class EngineModelServiceImpl extends AbstractSearchService<EngineModel, EngineModelDto, EngineModelSearchDto> {
    private static final String ENGINE_MODEL_TYPE_ENTITY_NAME = "engineModelType";
    private static final String ENGINE_MODEL_TYPE_ID = "id";
    private static final String POSITION = "position";
    private static final String IS_ACTIVE = "isActive";
    private final AircraftService aircraftService;
    private final EngineModelTypeService engineModelTypeService;

    /**
     * Autowired constructor
     *
     * @param repository             {@link AbstractRepository}
     * @param aircraftService        {@link AircraftService}
     * @param engineModelTypeService {@link EngineModelTypeService}
     */
    @Autowired
    public EngineModelServiceImpl(AbstractRepository<EngineModel> repository, AircraftService aircraftService,
                                  EngineModelTypeService engineModelTypeService) {
        super(repository);
        this.aircraftService = aircraftService;
        this.engineModelTypeService = engineModelTypeService;
    }

    /**
     * Responsible for converting entity to response view model
     *
     * @param engineModel {@link EngineModel}
     * @return engine model as view model
     */
    @Override
    protected EngineModelViewPayload convertToResponseDto(EngineModel engineModel) {
        return EngineModelViewPayload.builder()
                .engineModelId(engineModel.getId())
                .engineModelTypeId(engineModel.getEngineModelType().getId())
                .engineModelTypeName(engineModel.getEngineModelType().getName())
                .aircraftId(engineModel.getAircraft().getId())
                .aircraftName(engineModel.getAircraft().getAircraftName())
                .tsn(engineModel.getTsn())
                .csn(engineModel.getCsn())
                .etRating(engineModel.getEtRating())
                .serialNo(engineModel.getSerialNo())
                .position(engineModel.getPosition())
                .tsr(engineModel.getTsr())
                .csr(engineModel.getCsr())
                .tso(engineModel.getTso())
                .cso(engineModel.getCso())
                .isActive(engineModel.getIsActive())
                .build();
    }

    /**
     * Responsible for converting payload to entity
     *
     * @param engineModelDto {@link EngineModelDto}
     * @return               engine model
     */
    @Override
    protected EngineModel convertToEntity(EngineModelDto engineModelDto) {
        validateEngineModelDto(engineModelDto);

        EngineModel engineModel = new EngineModel();
        Aircraft aircraft = aircraftService.findById(engineModelDto.getAircraftId());
        EngineModelType engineModelType =
                engineModelTypeService.findActiveEngineModelType(engineModelDto.getEngineModelTypeId());

        engineModel.setAircraft(aircraft);
        engineModel.setEngineModelType(engineModelType);
        prepareNonRequiredEntity(engineModelDto, engineModel);

        return engineModel;
    }

    /**
     * Responsible for updating engine model entity
     *
     * @param engineModelDto {@link EngineModelDto}
     * @param engineModel    {@link EngineModel}
     * @return               updated engine model entity
     */
    @Override
    protected EngineModel updateEntity(EngineModelDto engineModelDto, EngineModel engineModel) {
        if (Objects.nonNull(engineModelDto.getEngineModelTypeId())) {
            engineModel.setEngineModelType(
                    engineModelTypeService.findActiveEngineModelType(engineModelDto.getEngineModelTypeId()));
        }

        if (Objects.nonNull(engineModelDto.getAircraftId())) {
            engineModel.setAircraft(aircraftService.findById(engineModelDto.getAircraftId()));
        }

        prepareNonRequiredEntity(engineModelDto, engineModel);

        return engineModel;
    }

    /**
     * Responsible for building search api query
     *
     * @param searchDto {@link EngineModelSearchDto}
     * @return          engine model specification
     */
    @Override
    protected Specification<EngineModel> buildSpecification(EngineModelSearchDto searchDto) {
        CustomSpecification<EngineModel> customSpecification = new CustomSpecification<>();
        Set<Long> engineModelTypeIdSet = new HashSet<>();
        if (Objects.nonNull(searchDto.getEngineModelTypeId())) {
            engineModelTypeIdSet.add(searchDto.getEngineModelTypeId());
        }

        return Specification.where(
                customSpecification.inSpecificationAtChild(engineModelTypeIdSet, ENGINE_MODEL_TYPE_ENTITY_NAME, ENGINE_MODEL_TYPE_ID))
                .and(customSpecification.likeSpecificationAtRoot(searchDto.getPosition(), POSITION)
                .and(customSpecification.equalSpecificationAtRoot(searchDto.getIsActive(), IS_ACTIVE))
        );
    }

    private void validateEngineModelDto(EngineModelDto engineModelDto) {
        if (Objects.isNull(engineModelDto.getEngineModelTypeId())) {
            throw new EngineeringManagementServerException(
                    ErrorId.ENGINE_MODEL_TYPE_ID_IS_REQUIRED, HttpStatus.BAD_REQUEST,
                    MDC.get(ApplicationConstant.TRACE_ID)
            );
        }

        if (Objects.isNull(engineModelDto.getAircraftId())) {
            throw new EngineeringManagementServerException(
                    ErrorId.AIRCRAFT_ID_IS_REQUIRED, HttpStatus.BAD_REQUEST,
                    MDC.get(ApplicationConstant.TRACE_ID)
            );
        }
    }

    private void prepareNonRequiredEntity(EngineModelDto engineModelDto, EngineModel engineModel) {
        engineModel.setTsn(engineModelDto.getTsn());
        engineModel.setCsn(engineModelDto.getCsn());
        engineModel.setEtRating(engineModelDto.getEtRating());
        engineModel.setSerialNo(engineModelDto.getSerialNo());
        engineModel.setPosition(engineModelDto.getPosition());
        engineModel.setTsr(engineModelDto.getTsr());
        engineModel.setCsr(engineModelDto.getCsr());
        engineModel.setTso(engineModelDto.getTso());
        engineModel.setCso(engineModelDto.getCso());
    }
}
