package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LatestAirTimeResponse {

    private Double totalHour;
    private Integer totalCycle;

    public LatestAirTimeResponse(Double totalHour, Integer totalCycle) {
        this.totalHour = totalHour;
        this.totalCycle = totalCycle;
    }

    private Double aircraftCheckDoneHour;
    private LocalDate aircraftCheckDoneDate;

    public LatestAirTimeResponse(Double aircraftCheckDoneHour, LocalDate aircraftCheckDoneDate) {
        this.aircraftCheckDoneHour = aircraftCheckDoneHour;
        this.aircraftCheckDoneDate = aircraftCheckDoneDate;
    }
}
