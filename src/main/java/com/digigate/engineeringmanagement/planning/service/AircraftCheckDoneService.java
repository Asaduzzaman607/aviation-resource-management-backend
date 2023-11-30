package com.digigate.engineeringmanagement.planning.service;

import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.planning.payload.request.AircraftCheckDoneSearchDto;
import org.springframework.data.domain.Pageable;

/**
 * AircraftCheckDone service
 *
 * @author Nafiul Islam
 */
public interface AircraftCheckDoneService {

    PageData searchAircraftCheckDone(AircraftCheckDoneSearchDto aircraftCheckDoneSearchDto,
                                     Pageable pageable);

}
