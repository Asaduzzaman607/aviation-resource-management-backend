package com.digigate.engineeringmanagement.planning.payload.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.time.LocalDate;
import java.time.YearMonth;

/**
 *Daily Utilization Report Dto
 *
 * @author Nafiul Islam
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class MonthlyUtilizationReport {

    private Double acHours;

    private Integer acCycle;

    private Double apuHours;

    private Integer apuCycle;


    @JsonIgnore
    private LocalDate date;

    private YearMonth yearMonth;

    private Double ratio;


    public MonthlyUtilizationReport(Double acHours, Integer acCycle, Double apuHours, Integer apuCycle, LocalDate date) {
        this.acHours = acHours;
        this.acCycle = acCycle;
        this.apuHours = apuHours;
        this.apuCycle = apuCycle;
        this.date = date;
    }

    public MonthlyUtilizationReport(Double acHours, Integer acCycle, Double apuHours, Integer apuCycle,
                                    YearMonth yearMonth, Double ratio) {
        this.acHours = acHours;
        this.acCycle = acCycle;
        this.apuHours = apuHours;
        this.apuCycle = apuCycle;
        this.yearMonth = yearMonth;
        this.ratio = ratio;

    }
}
