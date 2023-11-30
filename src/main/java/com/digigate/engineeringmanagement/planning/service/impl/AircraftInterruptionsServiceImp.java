package com.digigate.engineeringmanagement.planning.service.impl;

import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.common.service.AbstractService;
import com.digigate.engineeringmanagement.configurationmanagement.service.aircraftinformation.AircraftService;
import com.digigate.engineeringmanagement.planning.entity.AircraftInterruptions;
import com.digigate.engineeringmanagement.planning.payload.request.AircraftInterruptionsDto;
import com.digigate.engineeringmanagement.planning.payload.request.AircraftInterruptionsSearchDto;
import com.digigate.engineeringmanagement.planning.payload.response.AircraftInterruptionsViewModel;
import com.digigate.engineeringmanagement.planning.repository.AircraftInterruptionsRepository;
import com.digigate.engineeringmanagement.planning.service.AircraftInterruptionsService;
import com.digigate.engineeringmanagement.planning.service.AircraftLocationService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Objects;


/**
 * Aircraft Interruptions Service Implementation
 *
 * @author Nafiul Islam
 */
@Service
public class AircraftInterruptionsServiceImp extends AbstractService<AircraftInterruptions, AircraftInterruptionsDto>
        implements AircraftInterruptionsService {

    private final AircraftService aircraftService;

    private final AircraftLocationService aircraftLocationService;

    private final AircraftInterruptionsRepository interruptionsRepository;

    public AircraftInterruptionsServiceImp(AbstractRepository<AircraftInterruptions> repository,
                                           AircraftService aircraftService,
                                           AircraftLocationService aircraftLocationService,
                                           AircraftInterruptionsRepository interruptionsRepository) {
        super(repository);
        this.aircraftService = aircraftService;
        this.aircraftLocationService = aircraftLocationService;
        this.interruptionsRepository = interruptionsRepository;
    }


    @Override
    protected AircraftInterruptionsViewModel convertToResponseDto(AircraftInterruptions aircraftInterruptions) {
        AircraftInterruptionsViewModel aircraftInterruptionsViewModel = new AircraftInterruptionsViewModel();
        aircraftInterruptionsViewModel.setId(aircraftInterruptions.getId());
        aircraftInterruptionsViewModel.setAircraftId(aircraftInterruptions.getAircraftId());
        aircraftInterruptionsViewModel.setAircraftName(aircraftInterruptions.getAircraft().getAircraftName());
        aircraftInterruptionsViewModel.setLocationId(aircraftInterruptions.getLocationId());
        aircraftInterruptionsViewModel.setLocationName(aircraftInterruptions.getAircraftLocation().getName());
        aircraftInterruptionsViewModel.setDuration(aircraftInterruptions.getDuration().isPresent() ?
                aircraftInterruptions.getDuration().get() : null);
        aircraftInterruptionsViewModel.setDate(aircraftInterruptions.getDate());
        aircraftInterruptionsViewModel.setDefectDescription(aircraftInterruptions.getDefectDescription().isPresent() ?
                aircraftInterruptions.getDefectDescription().get() : null);
        aircraftInterruptionsViewModel.setRectDescription(aircraftInterruptions.getRectDescription().isPresent() ?
                aircraftInterruptions.getRectDescription().get() : null);
        aircraftInterruptionsViewModel.setIsActive(aircraftInterruptions.getIsActive());
        aircraftInterruptionsViewModel.setCreatedAt(aircraftInterruptions.getCreatedAt());
        aircraftInterruptionsViewModel.setAmlPageNo(aircraftInterruptions.getAmlPageNo());
        aircraftInterruptionsViewModel.setSeqNo(aircraftInterruptions.getSeqNo());
        return aircraftInterruptionsViewModel;
    }

    @Override
    protected AircraftInterruptions convertToEntity(AircraftInterruptionsDto aircraftInterruptionsDto) {
        return mapToEntity(aircraftInterruptionsDto, new AircraftInterruptions());
    }

    private AircraftInterruptions mapToEntity(AircraftInterruptionsDto aircraftInterruptionsDto,
                                              AircraftInterruptions aircraftInterruptions) {
        if (Objects.nonNull(aircraftInterruptionsDto.getAircraftId())) {
            aircraftInterruptions.setAircraft(aircraftService.findById(aircraftInterruptionsDto.getAircraftId()));
        }
        if (Objects.nonNull(aircraftInterruptionsDto.getLocationId())) {
            aircraftInterruptions.setAircraftLocation(aircraftLocationService
                    .findById(aircraftInterruptionsDto.getLocationId()));
        }
        if (Objects.nonNull(aircraftInterruptionsDto.getDate())) {
            aircraftInterruptions.setDate(aircraftInterruptionsDto.getDate());
        }
        if (Objects.nonNull(aircraftInterruptionsDto.getDefectDescription())) {
            aircraftInterruptions.setDefectDescription(aircraftInterruptionsDto.getDefectDescription());
        }
        if (Objects.nonNull(aircraftInterruptionsDto.getRectDescription())) {
            aircraftInterruptions.setRectDescription(aircraftInterruptionsDto.getRectDescription());
        }
        if (Objects.nonNull(aircraftInterruptionsDto.getDuration())) {
            aircraftInterruptions.setDuration(aircraftInterruptionsDto.getDuration());
        }
        if (Objects.nonNull(aircraftInterruptionsDto.getAmlPageNo())) {
            aircraftInterruptions.setAmlPageNo(aircraftInterruptionsDto.getAmlPageNo());
        }
        if (Objects.nonNull(aircraftInterruptionsDto.getSeqNo())) {
            aircraftInterruptions.setSeqNo(aircraftInterruptionsDto.getSeqNo());
        }
        return aircraftInterruptions;
    }

    @Override
    protected AircraftInterruptions updateEntity(AircraftInterruptionsDto dto, AircraftInterruptions entity) {
        return mapToEntity(dto, entity);
    }

    @Override
    public Boolean validateClientData(AircraftInterruptionsDto aircraftInterruptionsDto, Long id) {
        return true;
    }

    @Override
    public void updateActiveStatus(Long id, Boolean isActive) {
        super.updateActiveStatus(id, isActive);
    }


    @Override
    public PageData searchAircraftInterruptions(AircraftInterruptionsSearchDto aircraftInterruptionsSearchDto,
                                                Pageable pageable) {
        Page<AircraftInterruptionsViewModel> aircraftInterruptionsViewModels
                = interruptionsRepository.searchAircraftInterruptions(aircraftInterruptionsSearchDto.getAircraftId(),
                aircraftInterruptionsSearchDto.getStartDate(), aircraftInterruptionsSearchDto.getEndDate(),
                aircraftInterruptionsSearchDto.getIsActive(), pageable);

        return PageData.builder()
                .model(aircraftInterruptionsViewModels.getContent())
                .totalPages(aircraftInterruptionsViewModels.getTotalPages())
                .totalElements(aircraftInterruptionsViewModels.getTotalElements())
                .currentPage(pageable.getPageNumber() + 1)
                .build();
    }
}
