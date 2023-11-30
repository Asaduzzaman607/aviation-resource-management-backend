package com.digigate.engineeringmanagement.configurationmanagement.service.aircraftinformation;

import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.service.AbstractSearchService;
import com.digigate.engineeringmanagement.common.specification.CustomSpecification;
import com.digigate.engineeringmanagement.configurationmanagement.dto.request.aircraftinformation.AircraftModelDto;
import com.digigate.engineeringmanagement.configurationmanagement.entity.AircraftModel;
import com.digigate.engineeringmanagement.configurationmanagement.repository.aircraftinformation.AircraftModelRepository;
import com.digigate.engineeringmanagement.storemanagement.payload.request.search.IdQuerySearchDto;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.digigate.engineeringmanagement.common.constant.ApplicationConstant.*;
import static java.lang.Boolean.FALSE;

@Service
public class AircraftModelService extends AbstractSearchService<
        AircraftModel,
        AircraftModelDto,
        IdQuerySearchDto> {
    private final AircraftModelRepository aircraftModelRepository;
    private final AircraftService aircraftService;

    /**
     * Constructor parameterized
     *
     * @param aircraftModelRepository {@link AircraftModelRepository}
     * @param aircraftService         {@link AircraftService}
     */
    @Autowired
    public AircraftModelService(AircraftModelRepository aircraftModelRepository, @Lazy AircraftService aircraftService) {
        super(aircraftModelRepository);
        this.aircraftModelRepository = aircraftModelRepository;
        this.aircraftService = aircraftService;
    }

    /**
     * This method is responsible for converting aircraft model
     *
     * @param aircraftModel {@link AircraftModel}
     * @return full data of aircraft model {@link AircraftModel}
     */
    @Override
    protected AircraftModelDto convertToResponseDto(AircraftModel aircraftModel) {

        AircraftModelDto aircraftModelDto = new AircraftModelDto();
        aircraftModelDto.setId(aircraftModel.getId());
        aircraftModelDto.setAircraftModelName(aircraftModel.getAircraftModelName());
        aircraftModelDto.setDescription(aircraftModel.getDescription());
        aircraftModelDto.setCheckHourForA(aircraftModel.getCheckHourForA());
        aircraftModelDto.setCheckDaysForA(aircraftModel.getCheckDaysForA());
        aircraftModelDto.setCheckHourForC(aircraftModel.getCheckHourForC());
        aircraftModelDto.setCheckDaysForC(aircraftModel.getCheckDaysForC());
        return aircraftModelDto;
    }

    /**
     * This method is responsible for converting dto to entity
     *
     * @param aircraftModelDto {@link AircraftModelDto}
     * @return responding aircraft mdoel {@link AircraftModel}
     */
    @Override
    protected AircraftModel convertToEntity(AircraftModelDto aircraftModelDto) {
        return populateDtoToEntity(aircraftModelDto, new AircraftModel());
    }

    /**
     * This method is responsible for updating conversion
     *
     * @param aircraftModelDto {@link AircraftModelDto}
     * @param aircraftModel    {@link AircraftModel}
     * @return responding aircraft model {@link AircraftModel}
     */
    @Override
    protected AircraftModel updateEntity(AircraftModelDto aircraftModelDto, AircraftModel aircraftModel) {
        return populateDtoToEntity(aircraftModelDto, aircraftModel);
    }

    /**
     * This method is responsible update active status
     *
     * @param id {@link AircraftModel}
     * @param isActive {@link Boolean}
     */
    @Override
    public void updateActiveStatus(Long id, Boolean isActive) {
        if (isActive == FALSE && aircraftService.isPossibleInActiveForAircraftModel(id)) {
            throw new EngineeringManagementServerException(
                    ErrorId.AIRCRAFT_MODEL_INACTIVE_IS_NOT_POSSIBLE,
                    HttpStatus.PRECONDITION_FAILED,
                    MDC.get(TRACE_ID));
        }

        super.updateActiveStatus(id, isActive);
    }

    /**
     * This method is responsible for searching aircraft model by name
     *
     * @param searchDto {@link IdQuerySearchDto}
     * @return responding aircraft model specification
     */
    @Override
    protected Specification<AircraftModel> buildSpecification(IdQuerySearchDto searchDto) {
        return new CustomSpecification<AircraftModel>()
                .likeSpecificationAtRoot(searchDto.getQuery(), AIRCRAFT_MODEL_NAME);
    }

    /**
     * This method is responsible for populating update and create method;
     *
     * @param aircraftModelDto {@link AircraftModelDto}
     * @param aircraftModel {@link AircraftModel}
     * @return responding aircraft model
     */
    private AircraftModel populateDtoToEntity(AircraftModelDto aircraftModelDto, AircraftModel aircraftModel){
        /*This validation always would be first! Please be aware of validate function!*/
        validate(aircraftModelDto, aircraftModel);
        aircraftModel.setAircraftModelName(aircraftModelDto.getAircraftModelName());
        aircraftModel.setDescription(aircraftModelDto.getDescription());
        aircraftModel.setCheckHourForA(aircraftModelDto.getCheckHourForA());
        aircraftModel.setCheckDaysForA(aircraftModelDto.getCheckDaysForA());
        aircraftModel.setCheckHourForC(aircraftModelDto.getCheckHourForC());
        aircraftModel.setCheckDaysForC(aircraftModelDto.getCheckDaysForC());
        return aircraftModel;
    }

    /**
     * This method is responsible for validating aircraft model name
     * @param aircraftModelDto {@link AircraftModelDto}
     * @param old {@link AircraftModel}
     */
    private void validate(AircraftModelDto aircraftModelDto, AircraftModel old){
        List<AircraftModel> aircraftModelList =
                aircraftModelRepository.findByAircraftModelName(aircraftModelDto.getAircraftModelName());

        if(CollectionUtils.isNotEmpty(aircraftModelList) && (
                Objects.isNull(old) ||
                        aircraftModelList.size() > VALUE_ONE ||
                        !aircraftModelList.get(FIRST_INDEX).equals(old))) {
            throw EngineeringManagementServerException.badRequest(
                    ErrorId.AIRCRAFT_MODEL_NAME_ALREADY_EXIST);
        }
    }

    /**
     * This method is responsible finding AircraftModel By AircraftId
     *
     * @param aircraftId  Aircraft Id
     * @return id         Aircraft Model Id
     */
    public Long findAircraftModelIdByAircraftId(Long aircraftId) {
        Optional<Long> amId = aircraftModelRepository.findAircraftModelIdByAircraftId(aircraftId);
        if (amId.isPresent()) {
            return amId.get();
        } else {
            throw EngineeringManagementServerException.notFound(ErrorId.DATA_NOT_FOUND_BY_THIS_AIRCRAFT_ID);
        }
    }
}
