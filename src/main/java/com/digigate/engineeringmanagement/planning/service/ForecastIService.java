package com.digigate.engineeringmanagement.planning.service;

import com.digigate.engineeringmanagement.planning.payload.request.ForecastAircraftDto;
import com.digigate.engineeringmanagement.planning.payload.request.ForecastGenerateDto;
import com.digigate.engineeringmanagement.planning.payload.request.ForecastSearchDto;
import com.digigate.engineeringmanagement.planning.payload.response.ForecastViewModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface ForecastIService {
    ForecastAircraftDto generate(ForecastGenerateDto forecastGenerateDto, Long aircraftIdLong);

    Page<ForecastViewModel> search(ForecastSearchDto searchDto, Pageable pageable);
}
