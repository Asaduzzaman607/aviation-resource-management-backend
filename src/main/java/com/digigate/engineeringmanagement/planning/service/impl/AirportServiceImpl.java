package com.digigate.engineeringmanagement.planning.service.impl;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.planning.converter.AirportConverter;
import com.digigate.engineeringmanagement.planning.dto.request.AirportDto;
import com.digigate.engineeringmanagement.planning.dto.request.AirportSearchDto;
import com.digigate.engineeringmanagement.planning.entity.Airport;
import com.digigate.engineeringmanagement.planning.repository.AirportRepository;
import com.digigate.engineeringmanagement.planning.service.AirportService;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.AirportProjection;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.StoreIssueProjection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Airport service implementation
 */
@Service
public class AirportServiceImpl implements AirportService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AirportServiceImpl.class);


    private final AirportRepository airportRepository;

    /**
     * Autowired constructor
     *
     * @param airportRepository {@link AirportRepository}
     */
    @Autowired
    public AirportServiceImpl(AirportRepository airportRepository) {
        this.airportRepository = airportRepository;
    }

    /**
     * This method is responsible for finding specific airport by id
     *
     * @param id                        {@link Long}
     * @return Airport entity           {@link Airport}
     */
    @Override
    public Airport findById(Long id) {
        if (Objects.isNull(id)) {
            throw new EngineeringManagementServerException(
                    ErrorId.ID_IS_REQUIRED,
                    HttpStatus.BAD_REQUEST,
                    MDC.get(ApplicationConstant.TRACE_ID));
        }


        return airportRepository.findById(id).orElseThrow(()-> {
            throw new EngineeringManagementServerException(
                    ErrorId.AIRPORT_NOT_EXISTS,
                    HttpStatus.NOT_FOUND,
                    MDC.get(ApplicationConstant.TRACE_ID)
            );
        });
    }

    public List<AirportProjection> findByIdIn(Set<Long> airportIds) {
        return airportRepository.findAirportByIdIn(airportIds);
    }

    /**
     * This method is responsible for getting an active airport
     *
     * @param id                {@link Long}
     * @return airport entity   {@link Airport}
     */
    @Override
    public Airport findActiveAirportById(long id) {
        if (Objects.isNull(id)) {
            throw new EngineeringManagementServerException(
                    ErrorId.ID_IS_REQUIRED,
                    HttpStatus.BAD_REQUEST,
                    MDC.get(ApplicationConstant.TRACE_ID));
        }


        return airportRepository.findByIdAndIsActiveTrue(id).orElseThrow(()-> {
            throw new EngineeringManagementServerException(
                    ErrorId.AIRPORT_NOT_EXISTS,
                    HttpStatus.NOT_FOUND,
                    MDC.get(ApplicationConstant.TRACE_ID)
            );
        });
    }

    /**
     * This method is responsible for getting list of airports by ids
     *
     * @param ids
     * @return
     */
    @Override
    public List<Airport> findByIds(Set<Long> ids) {

        try {
            return airportRepository.findAllByIdInAndIsActiveTrue(ids);
        }catch (Exception e){
            throw new EngineeringManagementServerException(
                    ErrorId.AIRPORT_NOT_EXISTS,
                    HttpStatus.NOT_FOUND,
                    MDC.get(ApplicationConstant.TRACE_ID)
            );
        }
    }

    /**
     * This method is responsible for saving or updating Airport
     *
     * @param airportDto                            {@link  AirportDto}
     * @param id                                    {@link  Long}
     * @return newly saved or updated airport       {@link Airport}
     */
    @Override
    public Airport saveOrUpdate(AirportDto airportDto, Long id) {
        Airport airport = new Airport();
        if(Objects.isNull(id)){
            if(airportRepository.existsByIataCode(airportDto.getIataCode())){
                LOGGER.error("iata code already taken");
                throw new EngineeringManagementServerException(
                        ErrorId.IATA_CODE_ALREADY_EXISTS,
                        HttpStatus.BAD_REQUEST,
                        MDC.get(ApplicationConstant.TRACE_ID)
                );
            }
            airport = AirportConverter.convertDtoToEntity(airportDto, airport);
            airport.setIsActive(Boolean.TRUE);
        }
        else{
            airport = findById(id);

            if( airportDto.getIataCode().equals(airport.getIataCode()) == Boolean.FALSE ){
                if(airportRepository.existsByIataCode(airportDto.getIataCode())){
                    LOGGER.error("iata code already taken");
                    throw new EngineeringManagementServerException(
                            ErrorId.IATA_CODE_ALREADY_EXISTS,
                            HttpStatus.BAD_REQUEST,
                            MDC.get(ApplicationConstant.TRACE_ID)
                    );
                }
            }
            airport = AirportConverter.convertDtoToEntity(airportDto, airport);
        }
        try {
            return airportRepository.save(airport);
        }catch (Exception e){
            LOGGER.error("airport not saved");
            throw new EngineeringManagementServerException(
                    ErrorId.AIRPORT_NOT_SAVED,
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    MDC.get(ApplicationConstant.TRACE_ID)
            );
        }
    }

    /**
     *This method is for search airports by search criteria
     *
     * @param airportSearchDto              {@link  AirportSearchDto}
     * @param pageable                      {@link  Pageable}
     * @return airports                     {@link  Page<Airport>}
     */
    @Override
    public Page<Airport> searchAirports(AirportSearchDto airportSearchDto, Pageable pageable) {
        Page<Airport> airports = airportRepository.findAirportBySearchCriteria(
                airportSearchDto.getName(),
                airportSearchDto.getIataCode(),
                airportSearchDto.getIsActive(),
                pageable
        );
        return airports;
    }

    /**
     *This method is responsible for toggling active status of airport
     *
     * @param id {@link  Long}
     * @return toggled
     */
    @Override
    public Airport toggleActiveStatus(Long id) {
        Airport airport = findById(id);
        if(airport.getIsActive() == Boolean.TRUE){
            airport.setIsActive(Boolean.FALSE);
        }
        else{
            airport.setIsActive(Boolean.TRUE);
        }
        try {
            return airportRepository.save(airport);
        }
        catch (Exception e){
            LOGGER.error("Airport not toggled Active Status with id {}", id);
            throw  new EngineeringManagementServerException(
                    ErrorId.FAIL_TO_TOGGLE_ACTIVE_STATUS_AIRPORT,
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    MDC.get(ApplicationConstant.TRACE_ID)
            );
        }

    }


    /**
     * This method is responsible for getting all airports
     *
     * @return airport list {@link List<Airport>}
     */
    @Override
    public List<Airport> getAll() {
        return airportRepository.findAll();
    }

}
