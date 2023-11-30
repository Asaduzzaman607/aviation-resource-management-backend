package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ForecastResponseViewModel {
    private Long forecastId;
    private String name;
    List<ForecastAircraftViewModel> forecastAircraftViewModels;
}
