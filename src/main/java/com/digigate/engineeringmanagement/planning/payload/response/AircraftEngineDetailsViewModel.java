package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class AircraftEngineDetailsViewModel {
    private Double inHour;
    private Integer inCycle;
    private String modelName;
    private String regNo;
    private LocalDate date;
    private Double tat;
    private Integer tac;
    private Double tsn;
    private Integer csn;
}
