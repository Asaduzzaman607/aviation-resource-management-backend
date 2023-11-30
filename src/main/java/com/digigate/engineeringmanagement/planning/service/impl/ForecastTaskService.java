package com.digigate.engineeringmanagement.planning.service.impl;

import com.digigate.engineeringmanagement.planning.payload.request.ForecastTaskDto;
import com.digigate.engineeringmanagement.planning.repository.ForecastAircraftRepository;
import com.digigate.engineeringmanagement.planning.repository.ForecastTaskRepository;
import com.digigate.engineeringmanagement.planning.service.ForecastTaskIService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Set;

/**
 * Forecast Task Service Impl
 *
 * @author Masud Rana
 */
@Service
public class ForecastTaskService implements ForecastTaskIService {
    private final ForecastTaskRepository repository;

    /**
     * Parameterized constructor
     *
     * @param repository {@link ForecastAircraftRepository}
     */
    public ForecastTaskService(ForecastTaskRepository repository) {
        this.repository = repository;
    }

    /**
     * find forecastTask by forecastAircraftIds
     *
     * @param forecastAircraftIds {@link Set<Long>}
     * @return {@link Set<ForecastTaskDto>}
     */
    @Override
    public Set<ForecastTaskDto> findByForecastAircraftIdIn(Set<Long> forecastAircraftIds) {
        if (CollectionUtils.isEmpty(forecastAircraftIds)) {
            return Collections.emptySet();
        }
        return repository.findByForecastAircraftIdIn(forecastAircraftIds);
    }

}
