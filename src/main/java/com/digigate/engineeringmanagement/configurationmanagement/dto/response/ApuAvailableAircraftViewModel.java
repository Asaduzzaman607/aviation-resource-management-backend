package com.digigate.engineeringmanagement.configurationmanagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * ApuAvailable Aircraft ViewModel
 *
 * @author Nafiul Islam
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApuAvailableAircraftViewModel {
    private Long aircraftId;
    private String aircraftName;
    private String airframeSerial;
    private Double airFrameTotalTime;
    private Double bdTotalTime;
    private Integer airframeTotalCycle;
    private Integer bdTotalCycle;
    private Double dailyAverageHours;
    private Integer dailyAverageCycle;
    private Double dailyAverageApuHours;
    private Integer dailyAverageApuCycle;
    private Double totalApuHours;
    private Integer totalApuCycle;
}
