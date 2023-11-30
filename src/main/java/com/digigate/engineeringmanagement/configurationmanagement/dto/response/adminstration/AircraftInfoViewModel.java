package com.digigate.engineeringmanagement.configurationmanagement.dto.response.adminstration;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AircraftInfoViewModel {
    private Long id;
    private String aircraftName;
    private String airframeSerial;
    private Double acHour;
    private Integer acCycle;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate manufactureDate;
    private Double averageHours;
    private Integer averageCycle;
    private Double apuHours;
    private Integer apuCycle;
    private String engineType;
    private String propellerType;
    private String aircraftModelName;
    private LocalDate updatedTime;
    private LocalDate inductionDate;
}
