package com.digigate.engineeringmanagement.planning.service;

import com.digigate.engineeringmanagement.planning.payload.request.ForecastTaskPartDto;

import java.util.Set;

public interface ForecastTaskPartIService {
    Set<ForecastTaskPartDto> findByForecastTaskIdIn(Set<Long> forecastTaskIds);
}
