package com.digigate.engineeringmanagement.planning.service;

import com.digigate.engineeringmanagement.planning.payload.request.ForecastAircraftDto;

import java.util.List;

public interface ForecastAircraftIService {
    List<ForecastAircraftDto> findByForecastId(Long forecastId);
}
