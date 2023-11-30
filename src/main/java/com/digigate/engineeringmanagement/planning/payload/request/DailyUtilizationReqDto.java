package com.digigate.engineeringmanagement.planning.payload.request;


import com.digigate.engineeringmanagement.planning.entity.AmlFlightData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Transient;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class DailyUtilizationReqDto {

    private Long aircraftId;

    private LocalDate date;

    private DailyAirtimeCycle exAirtimeCycle;

    private DailyAirtimeCycle newAirTimeCycle;

    private Double apuUsedHrs;
    private Integer apuUsedCycle;

//    private Double apuHours;
//
//    private Integer apuCycles;


}
