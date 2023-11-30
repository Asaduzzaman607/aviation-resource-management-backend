package com.digigate.engineeringmanagement.planning.service;

import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.common.service.AbstractService;
import com.digigate.engineeringmanagement.configurationmanagement.entity.Aircraft;
import com.digigate.engineeringmanagement.configurationmanagement.service.aircraftinformation.AircraftService;
import com.digigate.engineeringmanagement.planning.entity.AircraftCabin;
import com.digigate.engineeringmanagement.planning.entity.Cabin;
import com.digigate.engineeringmanagement.planning.payload.request.AircraftCabinDto;
import com.digigate.engineeringmanagement.planning.payload.request.AircraftCabinSearchDto;
import com.digigate.engineeringmanagement.planning.payload.response.AircraftCabinViewModel;
import com.digigate.engineeringmanagement.planning.repository.AircraftCabinRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

/**
 * Aircraft cabin service
 *
 * @author Pranoy Das
 */
@Service
public class AircraftCabinService extends AbstractService<AircraftCabin, AircraftCabinDto> {
    private static final String DASH_SEPARATOR = " - ";
    private final AircraftService aircraftService;
    private final CabinService cabinService;
    private final AircraftCabinRepository aircraftCabinRepository;

    /**
     * Autowired constructor
     *  @param repository               {@link AbstractRepository}
     * @param aircraftService           {@link AircraftService}
     * @param cabinService              {@link CabinService}
     * @param aircraftCabinRepository   {@link AircraftCabinRepository}
     */
    public AircraftCabinService(AbstractRepository<AircraftCabin> repository, AircraftService aircraftService,
                                CabinService cabinService, AircraftCabinRepository aircraftCabinRepository) {
        super(repository);
        this.aircraftService = aircraftService;
        this.cabinService = cabinService;
        this.aircraftCabinRepository = aircraftCabinRepository;
    }

    /**
     * This method is responsible for searching aircraft cabin by search criteria
     *
     * @param aircraftCabinSearchDto {@link AircraftCabinSearchDto}
     * @param pageable               {@link Pageable}
     * @return                       return AircraftCabinViewModel as page data
     */
    public Page<AircraftCabinViewModel> searchAircraftCabins(AircraftCabinSearchDto aircraftCabinSearchDto, Pageable pageable) {
        return aircraftCabinRepository.findAircraftCabinBySearchCriteria(
                aircraftCabinSearchDto.getAircraftId(),
                aircraftCabinSearchDto.getCabinId(),
                aircraftCabinSearchDto.getIsActive(),
                pageable
        );
    }

    @Override
    protected AircraftCabinViewModel convertToResponseDto(AircraftCabin aircraftCabin) {
        return AircraftCabinViewModel.builder()
                .aircraftCabinId(aircraftCabin.getId())
                .aircraftId(aircraftCabin.getAircraft().getId())
                .aircraftName(aircraftCabin.getAircraft().getAircraftName())
                .cabinId(aircraftCabin.getCabin().getId())
                .cabinInfo(aircraftCabin.getCabin().getCode() + DASH_SEPARATOR + aircraftCabin.getCabin().getTitle())
                .numOfSeats(aircraftCabin.getNoOfSeats())
                .build();
    }

    @Override
    protected AircraftCabin convertToEntity(AircraftCabinDto aircraftCabinDto) {

        Cabin cabin = cabinService.findById(aircraftCabinDto.getCabinId());
        Aircraft aircraft = aircraftService.findById(aircraftCabinDto.getAircraftId());

        AircraftCabin aircraftCabin = new AircraftCabin();
        aircraftCabin.setAircraft(aircraft);
        aircraftCabin.setCabin(cabin);
        aircraftCabin.setNoOfSeats(aircraftCabinDto.getNoOfSeats());

        return aircraftCabin;
    }

    @Override
    protected AircraftCabin updateEntity(AircraftCabinDto dto, AircraftCabin entity) {
        if (Objects.nonNull(dto.getAircraftId())) {
            entity.setAircraft(aircraftService.findByIdUnfiltered(dto.getAircraftId()));
        }

        if (Objects.nonNull(dto.getCabinId())) {
            entity.setCabin(cabinService.findCabinById(dto.getCabinId()));
        }

        if (Objects.nonNull(dto.getNoOfSeats())) {
            entity.setNoOfSeats(dto.getNoOfSeats());
        }

        return entity;
    }

    /**
     * this method is responsible to validate client data
     *
     * @param aircraftCabinDto {@link  AircraftCabinDto}
     * @param id               {@link  Long}
     * @return {@link Boolean}
     */
    @Override
    public Boolean validateClientData(AircraftCabinDto aircraftCabinDto, Long id) {
        Optional<Long> aircraftCabinIdOptional = aircraftCabinRepository
                .findByAircraftIdAndCabinId(aircraftCabinDto.getAircraftId(), aircraftCabinDto.getCabinId());

        if (aircraftCabinIdOptional.isPresent() && (Objects.isNull(id) || !aircraftCabinIdOptional.get().equals(id))) {
            throw EngineeringManagementServerException.badRequest(ErrorId.AIRCRAFT_AND_CABIN_ALREADY_EXISTS);
        }

        return Boolean.TRUE;
    }

}
