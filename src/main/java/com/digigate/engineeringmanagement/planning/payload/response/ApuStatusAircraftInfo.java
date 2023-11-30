package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Apu Status Aircraft Info Model View
 *
 * @author Nafiul Islam
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ApuStatusAircraftInfo {
    private String partNo;
    private String serialNo;
    private Double aircraftInHour;
    private Integer aircraftInCycle;
    private Double installationTsn;
    private Integer installationCsn;
    private Double countFactor;
}
