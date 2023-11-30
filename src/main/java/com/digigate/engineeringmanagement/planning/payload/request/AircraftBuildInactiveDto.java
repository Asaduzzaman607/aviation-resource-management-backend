package com.digigate.engineeringmanagement.planning.payload.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AircraftBuildInactiveDto {

    @NotNull
    private Long id;

    private LocalDate outDate;

    @NotNull
    private Double aircraftOutHour;

    @NotNull
    private Integer aircraftOutCycle;

    private String  outRefMessage;

    private String  removalReason;

    private Boolean checkLowerPart;
    private String authNo;
    private String sign;
    private LocalDate createdDate;
}
