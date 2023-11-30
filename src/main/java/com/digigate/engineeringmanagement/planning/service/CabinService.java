package com.digigate.engineeringmanagement.planning.service;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.service.AbstractService;
import com.digigate.engineeringmanagement.planning.entity.AircraftCabin;
import com.digigate.engineeringmanagement.planning.entity.Cabin;
import com.digigate.engineeringmanagement.planning.payload.request.CabinDto;
import com.digigate.engineeringmanagement.planning.payload.response.CabinViewModel;
import com.digigate.engineeringmanagement.planning.repository.CabinRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Cabin service implementation
 *
 * @author Pranoy Das
 */
@Service
public class CabinService extends AbstractService<Cabin, CabinDto> {
    private static final Logger LOGGER = LoggerFactory.getLogger(CabinService.class);
    private final CabinRepository cabinRepository;

    /**
     * Autowired constructor
     *
     * @param cabinRepository   {@link CabinRepository}
     */
    @Autowired
    public CabinService(CabinRepository cabinRepository) {
        super(cabinRepository);
        this.cabinRepository = cabinRepository;
    }

    /**
     * responsible for create cabin
     *
     * @param cabinDto {@link CabinDto}
     * @return         cabin entity
     */
    @Override
    public Cabin create(CabinDto cabinDto) {
        List<Cabin> cabinList = cabinRepository.findAllByCodeOrTitle(cabinDto.getCode(), cabinDto.getTitle());

        if (CollectionUtils.isNotEmpty(cabinList)) {
            throw new EngineeringManagementServerException(
                    ErrorId.CABIN_CODE_AND_TITLE_MUST_BE_UNIQUE, HttpStatus.BAD_REQUEST,
                    MDC.get(ApplicationConstant.TRACE_ID)
            );
        }

        return super.create(cabinDto);
    }

    /**
     * responsible for updating cabin information
     *
     * @param cabinDto  {@link CabinDto}
     * @param id        id of cabin
     * @return          cabin entity
     */
    @Override
    public Cabin update(CabinDto cabinDto, Long id) {
        Cabin cabin = findCabinById(id);

        if (!cabinDto.getCode().equals(cabin.getCode()) && cabinRepository.existsByCode(cabinDto.getCode())) {
            throw EngineeringManagementServerException.badRequest(ErrorId.CABIN_CODE_ALREADY_EXISTS);
        }
        if (!cabinDto.getTitle().equals(cabin.getTitle()) && cabinRepository.existsByTitle(cabinDto.getTitle())) {
            throw EngineeringManagementServerException.badRequest(ErrorId.CABIN_TITLE_ALREADY_EXISTS);
        }
        if(cabinDto.getCode().equals(cabin.getCode()) && cabinDto.getTitle().equals(cabin.getTitle())){
            throw EngineeringManagementServerException.badRequest(ErrorId.CABIN_CODE_AND_TITLE_NOT_CHANGE);
        }
        return super.update(cabinDto, id);
    }

    /**
     * Responsible for getting all cabins
     *
     * @return list of cabin as view model
     */
    public List<CabinViewModel> getAllCabin() {
        List<Cabin> cabinList = cabinRepository.findAll();
        List<CabinViewModel> cabinViewModels = new ArrayList<>();

        cabinList.forEach(cabin -> {
            CabinViewModel cabinViewModel = new CabinViewModel();
            cabinViewModel.setCabinId(cabin.getId());
            cabinViewModel.setCodeTitle(cabin.getCode() + " - " + cabin.getTitle());
            cabinViewModel.setActiveStatus(cabin.getIsActive());
            cabinViewModels.add(cabinViewModel);
        });

        return cabinViewModels;
    }

    /**
     * responsible for changing cabin status
     *
     * @param cabinDto  {@link CabinDto}
     * @return          cabin as view model
     */
    public CabinViewModel changeActiveStatus(CabinDto cabinDto) {
        Cabin cabin = findCabinById(cabinDto.getCabinId());

        if (Objects.isNull(cabinDto.getActiveStatus()) || cabin.getIsActive() == cabinDto.getActiveStatus()) {
            throw new EngineeringManagementServerException(
                    ErrorId.FAILED_TO_CHANGE_CABIN_ACTIVE_STATUS, HttpStatus.BAD_REQUEST,
                    MDC.get(ApplicationConstant.TRACE_ID)
            );
        }

        if (CollectionUtils.isNotEmpty(cabin.getAircraftCabinSet())) {
            throw new EngineeringManagementServerException(
                    ErrorId.CABIN_ALREADY_USED, HttpStatus.BAD_REQUEST,
                    MDC.get(ApplicationConstant.TRACE_ID)
            );
        }

        try {
            cabin.setIsActive(cabinDto.getActiveStatus());
            cabinRepository.save(cabin);
        } catch (Exception ex) {
            LOGGER.error("Failed to change active status. Exception: {}", ex.getMessage());
            throw new EngineeringManagementServerException(
                    ErrorId.FAILED_TO_CHANGE_CABIN_ACTIVE_STATUS, HttpStatus.INTERNAL_SERVER_ERROR,
                    MDC.get(ApplicationConstant.TRACE_ID)
            );
        }

        return CabinViewModel.builder()
                .cabinId(cabin.getId())
                .codeTitle(cabin.getCode() + " - " + cabin.getTitle())
                .activeStatus(cabin.getIsActive())
                .build();
    }

    public Cabin findCabinById(Long cabinId) {
        return cabinRepository.findById(cabinId).orElseThrow(()->
                new EngineeringManagementServerException(ErrorId.DATA_NOT_FOUND, HttpStatus.NOT_FOUND,
                        MDC.get(ApplicationConstant.TRACE_ID))
        );
    }

    @Override
    protected <T> T convertToResponseDto(Cabin cabin) {
        return null;
    }

    @Override
    protected Cabin convertToEntity(CabinDto cabinDto) {
        return Cabin.builder()
                .code(cabinDto.getCode())
                .title(cabinDto.getTitle())
                .isActive(true)
                .build();
    }

    @Override
    protected Cabin updateEntity(CabinDto dto, Cabin cabin) {
        if (Objects.nonNull(dto.getCode())) {
            cabin.setCode(dto.getCode());
        }

        if (StringUtils.isNotBlank(dto.getTitle())) {
            cabin.setTitle(dto.getTitle());
        }

        return cabin;
    }
}
