package com.digigate.engineeringmanagement.planning.service;

import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.planning.dto.request.AircraftApusDto;
import com.digigate.engineeringmanagement.planning.entity.AircraftApus;
import com.digigate.engineeringmanagement.planning.payload.response.AircraftApusViewModel;

import org.springframework.data.domain.Pageable;

/**
 * AircraftApus Service
 *
 * @author Nafiul Islam
 */
public interface AircraftApusService {

    AircraftApus create(AircraftApusDto aircraftApusDto);

    AircraftApus update(AircraftApusDto aircraftApusDto, Long id);

    AircraftApusViewModel getAircraftApuDetailsById(Long id);

    PageData getAllAircraftApuDetails(Pageable pageable);
}
