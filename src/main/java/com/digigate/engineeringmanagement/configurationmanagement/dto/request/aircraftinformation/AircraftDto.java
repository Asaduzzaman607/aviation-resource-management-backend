package com.digigate.engineeringmanagement.configurationmanagement.dto.request.aircraftinformation;

import com.digigate.engineeringmanagement.common.payload.IDto;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AircraftDto implements IDto {
    private Long id;
    @NotBlank
    private String aircraftName;
    @NotBlank
    private String airframeSerial;
    @NotNull
    private Double airFrameTotalTime;
    @NotNull
    private Double bdTotalTime;
    @NotNull
    private Integer airframeTotalCycle;
    @NotNull
    private Integer bdTotalCycle;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate manufactureDate;
    @NotNull
    private Double dailyAverageHours;
    @NotNull
    private Integer dailyAverageCycle;
    @NotNull
    private Double dailyAverageApuHours;
    @NotNull
    private Integer dailyAverageApuCycle;
    @NotNull
    private Double totalApuHours;
    @NotNull
    private Integer totalApuCycle;

    @NotNull
    private Long aircraftModelId;

    private String engineType;
    private String propellerType;

    private Double aircraftCheckDoneHour;
    private LocalDate aircraftCheckDoneDate;

    private LocalDate inductionDate;
}
