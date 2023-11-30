package com.digigate.engineeringmanagement.planning.dto;

import com.digigate.engineeringmanagement.configurationmanagement.entity.Aircraft;
import com.digigate.engineeringmanagement.planning.entity.*;
import lombok.*;

import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForecastDataMap {
    Map<Long, Part> partMap;
    Map<Long, Ldnd> ldndMap;
    Map<Long, Aircraft> aircraftMap;
    Map<Long, ForecastTask> forecastTaskMap;
    Map<Long, ForecastTaskPart> forecastTaskPartMap;
    Map<Long, ForecastAircraft> forecastAircraftMap;
}
