package com.digigate.engineeringmanagement.planning.service;

import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.planning.payload.request.AircraftInterruptionsSearchDto;
import org.springframework.data.domain.Pageable;

/**
 * Aircraft Interruptions Service
 *
 * @author Nafiul Islam
 */
public interface AircraftInterruptionsService {


    PageData searchAircraftInterruptions(AircraftInterruptionsSearchDto aircraftInterruptionsSearchDto,
                                         Pageable pageable);
}
