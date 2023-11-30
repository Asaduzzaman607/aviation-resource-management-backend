package com.digigate.engineeringmanagement.configurationmanagement.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AircraftViewModel {
    private Long id;
    private String aircraftName;
    private String airframeSerial;
    private Double airFrameTotalTime;
    private Double bdTotalTime;
    private Integer airframeTotalCycle;
    private Integer bdTotalCycle;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate manufactureDate;
    private Double dailyAverageHours;
    private Integer dailyAverageCycle;
    private Double dailyAverageApuHours;
    private Integer dailyAverageApuCycle;
    private Double totalApuHours;
    private Integer totalApuCycle;
    private Long aircraftModelId;
    private String aircraftModelName;
    private String engineType;
    private String propellerType;
    private Double aircraftCheckDoneHour;
    private LocalDate aircraftCheckDoneDate;
    private LocalDate inductionDate;


    public AircraftViewModel(Long aircraftModelId) {
        this.aircraftModelId = aircraftModelId;
    }
}
