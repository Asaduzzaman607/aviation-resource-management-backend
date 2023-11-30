package com.digigate.engineeringmanagement.planning.payload.request;

import com.digigate.engineeringmanagement.configurationmanagement.entity.AircraftModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Month;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AcStatisticsDto {
    private Long id;
    private AircraftModel aircraftModel;
    private Long aircraftModelId;
    private Integer totalServiceDay;
    private Double totalFlightHour;
    private Double totalFlightCycle;
    private Integer month;
    private Integer year;
}
