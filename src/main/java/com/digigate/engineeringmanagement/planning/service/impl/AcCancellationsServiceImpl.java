package com.digigate.engineeringmanagement.planning.service.impl;

import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.common.service.AbstractService;
import com.digigate.engineeringmanagement.configurationmanagement.service.aircraftinformation.AircraftModelService;
import com.digigate.engineeringmanagement.planning.entity.AcCancellations;
import com.digigate.engineeringmanagement.planning.payload.request.AcCancellationsDto;
import com.digigate.engineeringmanagement.planning.payload.request.AcCancellationsSearchDto;
import com.digigate.engineeringmanagement.planning.payload.response.AcCancellationsReportViewModel;
import com.digigate.engineeringmanagement.planning.payload.response.AcCancellationsViewModel;
import com.digigate.engineeringmanagement.planning.repository.AcCancellationsRepository;
import com.digigate.engineeringmanagement.planning.service.AcCancellationsService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;

/**
 * AcCancellations Service Implementation
 *
 * @author Nafiul Islam
 */
@Service
public class AcCancellationsServiceImpl extends AbstractService<AcCancellations, AcCancellationsDto>
        implements AcCancellationsService {

    private final AircraftModelService aircraftModelService;

    private final AcCancellationsRepository acCancellationsRepository;

    public AcCancellationsServiceImpl(AbstractRepository<AcCancellations> repository,
                                      AircraftModelService aircraftModelService,
                                      AcCancellationsRepository acCancellationsRepository) {
        super(repository);
        this.aircraftModelService = aircraftModelService;
        this.acCancellationsRepository = acCancellationsRepository;
    }


    @Override
    protected AcCancellationsViewModel convertToResponseDto(AcCancellations acCancellations) {

        return AcCancellationsViewModel.builder()
                .id(acCancellations.getId())
                .aircraftModelId(acCancellations.getAircraftModelId())
                .aircraftModelName(acCancellations.getAircraftModel().getAircraftModelName())
                .date(acCancellations.getDate())
                .cancellationTypeEnum(acCancellations.getCancellationTypeEnum())
                .cancellationTypeId(acCancellations.getCancellationTypeEnum().getCancellationType())
                .isActive(acCancellations.getIsActive())
                .createdAt(acCancellations.getCreatedAt())
                .build();
    }

    @Override
    protected AcCancellations convertToEntity(AcCancellationsDto acCancellationsDto) {
        return populateEntity(acCancellationsDto, new AcCancellations());
    }

    private AcCancellations populateEntity(AcCancellationsDto acCancellationsDto, AcCancellations acCancellations) {
        acCancellations.setAircraftModel(aircraftModelService.findById(acCancellationsDto.getAircraftModelId()));
        acCancellations.setCancellationTypeEnum(acCancellationsDto.getCancellationTypeEnum());
        acCancellations.setDate(acCancellationsDto.getDate());
        return acCancellations;
    }

    @Override
    protected AcCancellations updateEntity(AcCancellationsDto dto, AcCancellations entity) {
        return populateEntity(dto, entity);
    }

    @Override
    public Boolean validateClientData(AcCancellationsDto acCancellationsDto, Long id) {
        return true;
    }

    @Override
    public void updateActiveStatus(Long id, Boolean isActive){
        super.updateActiveStatus(id, isActive);
    }

    @Override
    public PageData searchAircraftCancellation(AcCancellationsSearchDto acCancellationsSearchDto, Pageable pageable) {

        Page<AcCancellationsReportViewModel> acCancellationsViewModelPage
                = acCancellationsRepository.searchAircraftCancellation(acCancellationsSearchDto.getAircraftModelId(),
                acCancellationsSearchDto.getStartDate(), acCancellationsSearchDto.getEndDate(),
                acCancellationsSearchDto.getIsActive(), pageable);

        return PageData.builder()
                .model(acCancellationsViewModelPage.getContent())
                .totalPages(acCancellationsViewModelPage.getTotalPages())
                .totalElements(acCancellationsViewModelPage.getTotalElements())
                .currentPage(pageable.getPageNumber() + 1)
                .build();
    }
}
