package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Daily Utilization Report Dto
 *
 * @author Nafiul Islam
 */
@AllArgsConstructor
@Getter
@Setter
public class DailyUtilizationReport {
    private Double usedHrs;
    private Integer usedCyc;

    private Double tat;

    private Integer tac;

    private Double apuUsedHrs;

    private Integer apuUsedCycle;

    private LocalDate date;

    public DailyUtilizationReport(Double usedHrs, Integer usedCyc, Double tat, Integer tac, Double apuUsedHrs, Integer apuUsedCycle, LocalDate date, Double eng1OilUplift, Double eng2OilUplift) {
        this.usedHrs = (Objects.isNull(usedHrs))? 0.0 : usedHrs;
        this.usedCyc = (Objects.isNull(usedCyc))? 0 : usedCyc;
        this.tat = tat;
        this.tac = tac;
        this.apuUsedHrs = (Objects.isNull(apuUsedHrs))? 0 : apuUsedHrs;
        this.apuUsedCycle = (Objects.isNull(apuUsedCycle))? 0 : apuUsedCycle;
        this.date = date;
        this.eng1OilUplift = (Objects.isNull(eng1OilUplift))? 0 : eng1OilUplift;
        this.eng2OilUplift = (Objects.isNull(eng2OilUplift))? 0 : eng1OilUplift;;
    }

    private Double eng1OilUplift;

    private Double eng2OilUplift;

    private Double eng1Tsn;

    private Integer eng1Csn;

    private Double eng2Tsn;

    private Integer eng2Csn;

    private Double nlgTsn;

    private Integer nlgCsn;

    private Double lhMlgTsn;

    private Integer lhMlgCsn;

    private Double rhMlgTsn;

    private Integer rhMlgCsn;

    private Double apuTsn;

    private Integer apuCsn;

}
