package com.digigate.engineeringmanagement.planning.service;

import com.digigate.engineeringmanagement.planning.dto.request.AirportDto;
import com.digigate.engineeringmanagement.planning.dto.request.AirportSearchDto;
import com.digigate.engineeringmanagement.planning.entity.Airport;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.AirportProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

public interface AirportService {
    Airport findById(Long id);
    Airport findActiveAirportById(long id);
    List<Airport> findByIds(Set<Long> ids);
    Airport saveOrUpdate(AirportDto airportDto, Long id);
    Page<Airport> searchAirports(AirportSearchDto airportSearchDto, Pageable pageable);
    Airport toggleActiveStatus(Long id);
    List<Airport> getAll();
    List<AirportProjection> findByIdIn(Set<Long> airportIds);
}
