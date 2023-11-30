package com.digigate.engineeringmanagement.planning.payload.response;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.time.LocalDate;
import java.time.YearMonth;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class YearlyUtilizationReport {

    private Double acHours;

    private Integer acCycle;

    private Double apuHours;

    private Integer apuCycle;

    private Integer year;

    public YearlyUtilizationReport(Double acHours, Integer acCycle, Double apuHours, Integer apuCycle, Integer year) {
        this.acHours = acHours;
        this.acCycle = acCycle;
        this.apuHours = apuHours;
        this.apuCycle = apuCycle;
        this.year = year;
    }

    private Double ratio;
}
