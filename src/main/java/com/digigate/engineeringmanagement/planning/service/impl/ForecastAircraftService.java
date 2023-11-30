package com.digigate.engineeringmanagement.planning.service.impl;

import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.planning.payload.request.ForecastAircraftDto;
import com.digigate.engineeringmanagement.planning.repository.ForecastAircraftRepository;
import com.digigate.engineeringmanagement.planning.service.ForecastAircraftIService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * ForecastAircraftService
 *
 * @author Masud Rana
 */
@Service
public class ForecastAircraftService implements ForecastAircraftIService {
    private final ForecastAircraftRepository repository;

    /**
     * Parameterized constructor
     *
     * @param repository {@link ForecastAircraftRepository}
     */
    public ForecastAircraftService(ForecastAircraftRepository repository) {
        this.repository = repository;
    }

    /**
     * Find ForecastAircraft list by forecastId
     *
     * @param forecastId {@link Long}
     * @return {@link List<ForecastAircraftDto>}
     */
    @Override
    public List<ForecastAircraftDto> findByForecastId(Long forecastId) {
        if (Objects.isNull(forecastId)) {
            throw EngineeringManagementServerException.badRequest(ErrorId.FORECAST_ID_IS_REQUIRED);
        }
        return repository.findByForecastId(forecastId);
    }
}
