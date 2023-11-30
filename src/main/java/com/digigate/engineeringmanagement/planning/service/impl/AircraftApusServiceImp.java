package com.digigate.engineeringmanagement.planning.service.impl;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.configurationmanagement.entity.Aircraft;
import com.digigate.engineeringmanagement.configurationmanagement.service.aircraftinformation.AircraftService;
import com.digigate.engineeringmanagement.planning.dto.request.AircraftApusDto;
import com.digigate.engineeringmanagement.planning.entity.AircraftApus;
import com.digigate.engineeringmanagement.planning.payload.response.AircraftApusViewModel;
import com.digigate.engineeringmanagement.planning.repository.AircraftApusRepository;
import com.digigate.engineeringmanagement.planning.service.AircraftApusService;
import org.slf4j.MDC;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import java.util.Objects;
import java.util.Optional;

/**
 * AircraftApus Service Implementation
 *
 * @author Nafiul Islam
 */
@Service
public class AircraftApusServiceImp implements AircraftApusService {

    private final AircraftApusRepository apusRepository;
    private final AircraftService aircraftService;

    public AircraftApusServiceImp(AircraftApusRepository apusRepository, AircraftService aircraftService) {
        this.apusRepository = apusRepository;
        this.aircraftService = aircraftService;
    }

    @Override
    public AircraftApus create(AircraftApusDto aircraftApusDto) {
        checkAircraftApu(aircraftApusDto.getAircraftId());
        checkDuplicateAircraftApus(aircraftApusDto.getAircraftId());
        AircraftApus aircraftApus = convertToEntity(aircraftApusDto);
        return apusRepository.save(aircraftApus);
    }

    @Override
    public AircraftApus update(AircraftApusDto aircraftApusDto, Long id) {
        Optional<AircraftApus> aircraftApus = apusRepository.findById(id);
        if(ObjectUtils.isEmpty(aircraftApus)){
            throw new EngineeringManagementServerException(ErrorId.NO_AIRCRAFT_APUS_RECORD_FOUND,
                    HttpStatus.BAD_REQUEST, MDC.get(ApplicationConstant.TRACE_ID));
        }else if(aircraftApus.isPresent()){
            aircraftApus.get().setDate(aircraftApusDto.getDate());
            aircraftApus.get().setModel(aircraftApusDto.getModel());
            aircraftApus.get().setCsn(aircraftApusDto.getCsn());
            aircraftApus.get().setTsn(aircraftApusDto.getTsn());
            aircraftApus.get().setTsr(aircraftApusDto.getTsr());
            aircraftApus.get().setCsr(aircraftApusDto.getCsr());
            aircraftApus.get().setStatus(aircraftApusDto.getStatus());
        }
        return apusRepository.save(aircraftApus.get());
    }

    @Override
    public AircraftApusViewModel getAircraftApuDetailsById(Long id) {

        Optional<AircraftApus> aircraftApus = apusRepository.findById(id);
        if(aircraftApus.isEmpty()){
            throw new EngineeringManagementServerException(ErrorId.DATA_NOT_FOUND,
                    HttpStatus.BAD_REQUEST, MDC.get(ApplicationConstant.TRACE_ID));
        }

        return  AircraftApusViewModel.builder()
                .id(aircraftApus.get().getId())
                .aircraftId(aircraftApus.get().getAircraftId())
                .aircraftName( aircraftApus.get().getAircraft().getAircraftName())
                .model(aircraftApus.get().getModel())
                .date(aircraftApus.get().getDate())
                .tsn(aircraftApus.get().getTsn())
                .csn( aircraftApus.get().getCsn())
                .tsr(aircraftApus.get().getTsr())
                .csr(aircraftApus.get().getCsr())
                .status(aircraftApus.get().getStatus())
                .build();
    }

    @Override
    public PageData getAllAircraftApuDetails(Pageable pageable) {

        Page<AircraftApusViewModel> aircraftApuses = apusRepository.findAllApu(pageable);

        return PageData.builder()
                .model(aircraftApuses.getContent())
                .totalPages(aircraftApuses.getTotalPages())
                .totalElements(aircraftApuses.getTotalElements())
                .currentPage(pageable.getPageNumber() + 1)
                .build();
    }

    private void checkAircraftApu(Long aircraftId) {
        Aircraft aircraft = aircraftService.findById(aircraftId);
        if(aircraft.getTotalApuHours()<0){
            throw new EngineeringManagementServerException(ErrorId.THIS_AIRCRAFT_HAS_NO_APU,
                    HttpStatus.BAD_REQUEST, MDC.get(ApplicationConstant.TRACE_ID));
        }
    }

    private void checkDuplicateAircraftApus(Long aircraftId) {
        Optional<Long> duplicateAircraftApuId = apusRepository.findDuplicateAircraftApuId(aircraftId);
        if( !(ObjectUtils.isEmpty(duplicateAircraftApuId))){
            throw new EngineeringManagementServerException(ErrorId.DUPLICATE_AVAILABLE_AIRCRAFT_APUS_FOUND,
                    HttpStatus.BAD_REQUEST, MDC.get(ApplicationConstant.TRACE_ID));
        }
    }

    private AircraftApus convertToEntity(AircraftApusDto aircraftApusDto){
        return maptoEntity(aircraftApusDto,new AircraftApus());
    }

    private AircraftApus maptoEntity(AircraftApusDto aircraftApusDto, AircraftApus aircraftApus) {
        if (Objects.nonNull(aircraftApusDto.getAircraftId())) {
            Aircraft aircraft = aircraftService.findById(aircraftApusDto.getAircraftId());
            aircraftApus.setAircraft(aircraft);
        }
        if(Objects.nonNull(aircraftApusDto.getModel())){
            aircraftApus.setModel(aircraftApusDto.getModel());
        }
        if(Objects.nonNull(aircraftApusDto.getDate())){
            aircraftApus.setDate(aircraftApusDto.getDate());
        }
        if(Objects.nonNull(aircraftApusDto.getStatus())){
            aircraftApus.setStatus(aircraftApusDto.getStatus());
        }
        if(Objects.nonNull(aircraftApusDto.getTsn())){
            aircraftApus.setTsn(aircraftApusDto.getTsn());
        }
        if(Objects.nonNull(aircraftApusDto.getCsn())){
            aircraftApus.setCsn(aircraftApusDto.getCsn());
        }
        if(Objects.nonNull(aircraftApusDto.getCsr())){
            aircraftApus.setCsr(aircraftApusDto.getCsr());
        }
        if(Objects.nonNull(aircraftApusDto.getTsr())){
            aircraftApus.setTsr(aircraftApusDto.getTsr());
        }

        return aircraftApus;
    }
}
