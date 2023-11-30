package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Apu Removed Status Aircraft Info Model View
 *
 * @author Nafiul Islam
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ApuRemovedStatusAircraftInfo {
    private String partNo;
    private String serialNo;
    private Double aircraftInHour;
    private Integer aircraftInCycle;
    private Double aircraftOutHour;
    private Integer aircraftOutCycle;
    private LocalDate outDate;
    private Double installationTsn;
    private Integer installationCsn;
    private Double countFactor;
}
