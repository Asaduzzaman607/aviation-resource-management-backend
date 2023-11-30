package com.digigate.engineeringmanagement.planning.service;

import com.digigate.engineeringmanagement.planning.entity.Model;
import com.digigate.engineeringmanagement.planning.payload.response.ModelResponseByAircraftDto;
import java.util.List;
import java.util.Set;

public interface ModelIService {
    List<ModelResponseByAircraftDto> getModelListByAircraft(Long aircraftId);

    List<ModelResponseByAircraftDto> getModelListByAircraftModelId(Long aircraftModelId);

    Set<Model> findAllByAircraftModelId(Long aircraftModelId);
}
