package com.digigate.engineeringmanagement.planning.service;

import com.digigate.engineeringmanagement.planning.dto.request.AircraftEffectivityDto;
import com.digigate.engineeringmanagement.planning.entity.AircraftEffectivity;
import com.digigate.engineeringmanagement.planning.payload.request.AircraftEffectivityTaskDto;

import java.util.List;
import java.util.Set;

/**
 * Aircraft Effectivity Interface
 *
 * @author Sayem Hasnat
 */
public interface AircraftEffectivityIService {
    List<AircraftEffectivityDto> getAircraftEffectivityByAircraft(Long aircraftId);

    AircraftEffectivity getAircraftEffectiveByAircraftAndTask(Long aircraftId, Long taskId);

    List<AircraftEffectivityTaskDto> findAllAircraftEffectivityTaskDtoByAircraftId(Long aircraftId);

    void saveItemList(List<AircraftEffectivity> aircraftEffectivityList);

    List<AircraftEffectivity> findAllAircraftEffectivityById(Set<Long> aircraftEffectivityIds);
}
