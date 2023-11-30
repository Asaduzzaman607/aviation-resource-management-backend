package com.digigate.engineeringmanagement.planning.payload.request;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DailyAirtimeCycle {

    private Double hour;
    private Integer cycle;
    private Integer tac;
    private Double tat;
    private Double apuHour;
    private Integer apuCycle;

    private Long flightDataId;

    public DailyAirtimeCycle(Double apuHour, Integer apuCycle, Long flightDataId) {
        this.apuHour = apuHour;
        this.apuCycle = apuCycle;
        this.flightDataId = flightDataId;
    }

    private Double engineOil1;

    private Double engineOil2;
}
