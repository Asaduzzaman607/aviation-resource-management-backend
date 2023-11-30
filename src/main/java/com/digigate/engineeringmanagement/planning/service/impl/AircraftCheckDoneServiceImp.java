package com.digigate.engineeringmanagement.planning.service.impl;

import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.common.service.AbstractService;
import com.digigate.engineeringmanagement.common.util.NumberUtil;
import com.digigate.engineeringmanagement.configurationmanagement.entity.Aircraft;
import com.digigate.engineeringmanagement.configurationmanagement.service.aircraftinformation.AircraftService;
import com.digigate.engineeringmanagement.planning.constant.CheckType;
import com.digigate.engineeringmanagement.planning.entity.AircraftCheckDone;
import com.digigate.engineeringmanagement.planning.payload.request.AircraftCheckDoneDto;
import com.digigate.engineeringmanagement.planning.payload.request.AircraftCheckDoneSearchDto;
import com.digigate.engineeringmanagement.planning.payload.response.AircraftCheckDoneViewModel;
import com.digigate.engineeringmanagement.planning.repository.AircraftCheckDoneRepository;
import com.digigate.engineeringmanagement.planning.service.AircraftCheckDoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * AircraftCheckDone service implementation
 *
 * @author Nafiul Islam
 */

@Service
public class AircraftCheckDoneServiceImp extends AbstractService<AircraftCheckDone, AircraftCheckDoneDto>
        implements AircraftCheckDoneService {

    private final AircraftCheckDoneRepository aircraftCheckDoneRepository;

    private final AircraftService aircraftService;

    @Autowired
    public AircraftCheckDoneServiceImp(AbstractRepository<AircraftCheckDone> repository,
                                       AircraftCheckDoneRepository aircraftCheckDoneRepository,
                                       AircraftService aircraftService) {
        super(repository);
        this.aircraftCheckDoneRepository = aircraftCheckDoneRepository;
        this.aircraftService = aircraftService;
    }

    @Override
    protected AircraftCheckDoneViewModel convertToResponseDto(AircraftCheckDone aircraftCheckDone) {
        Aircraft aircraft = aircraftService.findById(aircraftCheckDone.getAircraftId());
        return AircraftCheckDoneViewModel.builder()
                .id(aircraftCheckDone.getId())
                .aircraftName(aircraft.getAircraftName())
                .aircraftId(aircraftCheckDone.getAircraftId())
                .aircraftCheckDoneDate(aircraftCheckDone.getAircraftCheckDoneDate())
                .aircraftCheckDoneHour(aircraftCheckDone.getAircraftCheckDoneHour())
                .isActive(aircraftCheckDone.getIsActive())
                .checkType(aircraftCheckDone.getCheckType().getVal())
                .build();
    }

    @Override
    protected AircraftCheckDone convertToEntity(AircraftCheckDoneDto aircraftCheckDoneDto) {
        return mapToEntity(aircraftCheckDoneDto, new AircraftCheckDone());
    }

    private AircraftCheckDone mapToEntity(AircraftCheckDoneDto aircraftCheckDoneDto,
                                          AircraftCheckDone aircraftCheckDone) {

        Aircraft aircraft = aircraftService.findById(aircraftCheckDoneDto.getAircraftId());
        aircraftCheckDone.setAircraft(aircraft);
        aircraftCheckDone.setAircraftCheckDoneHour(aircraftCheckDoneDto.getAircraftCheckDoneHour());
        aircraftCheckDone.setAircraftCheckDoneDate(aircraftCheckDoneDto.getAircraftCheckDoneDate());
        aircraftCheckDone.setCheckType(CheckType.getVal(aircraftCheckDoneDto.getCheckType()));

        if (Objects.nonNull(aircraftCheckDoneDto.getAircraftCheckDoneHour())) {
            if (aircraft.getAirFrameTotalTime() < aircraftCheckDoneDto.getAircraftCheckDoneHour()) {
                throw EngineeringManagementServerException.badRequest(ErrorId.INVALID_CHECK_DONE);
            }
            if (Objects.isNull(aircraft.getAircraftCheckDoneHour())
                    || aircraftCheckDoneDto.getAircraftCheckDoneHour() > aircraft.getAircraftCheckDoneHour()) {
                aircraft.setAircraftCheckDoneHour(aircraftCheckDoneDto.getAircraftCheckDoneHour());
            }
        }

        if (Objects.nonNull(aircraftCheckDoneDto.getAircraftCheckDoneDate())) {
            if (aircraft.getAircraftCheckDoneDate() == null
                    || aircraftCheckDoneDto.getAircraftCheckDoneDate().isAfter(aircraft.getAircraftCheckDoneDate())) {
                aircraft.setAircraftCheckDoneDate(aircraftCheckDoneDto.getAircraftCheckDoneDate());
            }
        }

        return aircraftCheckDone;
    }

    @Override
    protected AircraftCheckDone updateEntity(AircraftCheckDoneDto aircraftCheckDoneDto,
                                             AircraftCheckDone aircraftCheckDone) {
        return mapToEntity(aircraftCheckDoneDto, aircraftCheckDone);
    }

    @Override
    public PageData searchAircraftCheckDone(AircraftCheckDoneSearchDto aircraftCheckDoneSearchDto,
                                            Pageable pageable) {

        Page<AircraftCheckDoneViewModel> aircraftCheckDoneViewModelPage
                = aircraftCheckDoneRepository.findAllByDate(aircraftCheckDoneSearchDto.getAircraftId(),
                aircraftCheckDoneSearchDto.getDate(), aircraftCheckDoneSearchDto.getIsActive(), pageable);


        aircraftCheckDoneViewModelPage.getContent().forEach(d->{
            d.setCheckType(d.getCheckTypeEnum().getVal());
        });

        return PageData.builder()
                .model(aircraftCheckDoneViewModelPage.getContent())
                .totalPages(aircraftCheckDoneViewModelPage.getTotalPages())
                .totalElements(aircraftCheckDoneViewModelPage.getTotalElements())
                .currentPage(pageable.getPageNumber() + 1)
                .build();
    }

}
