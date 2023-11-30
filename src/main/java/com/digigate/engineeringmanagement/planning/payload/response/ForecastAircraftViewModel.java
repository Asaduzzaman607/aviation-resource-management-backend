package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.*;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ForecastAircraftViewModel {
    private Long forecastAircraftId;
    private Long aircraftId;
    private String aircraftName;
    List<ForecastTaskViewModel> forecastTaskViewModels;
}
