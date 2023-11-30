package com.digigate.engineeringmanagement.planning.service.impl;

import com.digigate.engineeringmanagement.planning.payload.request.ForecastTaskPartDto;
import com.digigate.engineeringmanagement.planning.repository.ForecastTaskPartRepository;
import com.digigate.engineeringmanagement.planning.service.ForecastTaskPartIService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Set;

/**
 * ForecastTaskPartService
 *
 * @author Masud Rana
 */
@Service
public class ForecastTaskPartService implements ForecastTaskPartIService {
    private final ForecastTaskPartRepository repository;

    /**
     * Parameterized constructor
     *
     * @param repository {@link ForecastTaskPartRepository}
     */
    public ForecastTaskPartService(ForecastTaskPartRepository repository) {
        this.repository = repository;
    }

    /**
     * Find ForecastTaskPartDto list by forecastTaskIds
     *
     * @param forecastTaskIds {@link Set<Long>}
     * @return {@link Set<ForecastTaskPartDto>}
     */
    @Override
    public Set<ForecastTaskPartDto> findByForecastTaskIdIn(Set<Long> forecastTaskIds) {
        if (CollectionUtils.isEmpty(forecastTaskIds)) {
            return Collections.emptySet();
        }
        return repository.findByForecastTaskIdIn(forecastTaskIds);
    }

}
