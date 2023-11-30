package com.digigate.engineeringmanagement.planning.service;

import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.planning.payload.request.AcCancellationsSearchDto;
import org.springframework.data.domain.Pageable;

/**
 * AcCancellations Service
 *
 * @author Nafiul Islam
 */
public interface AcCancellationsService {

    PageData searchAircraftCancellation(AcCancellationsSearchDto acCancellationsSearchDto, Pageable pageable);
}
