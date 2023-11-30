package com.digigate.engineeringmanagement.planning.service;

import com.digigate.engineeringmanagement.planning.payload.request.ForecastTaskDto;

import java.util.Set;

public interface ForecastTaskIService {
    Set<ForecastTaskDto> findByForecastAircraftIdIn(Set<Long> forecastAircraftIds);
}
