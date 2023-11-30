package com.digigate.engineeringmanagement.planning.payload.response;

import com.digigate.engineeringmanagement.planning.constant.ItemColorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class AircraftData {
    private Long aircraftId;
    private LocalDate asOfDate;
    private String aircraftName;
    private String aircraftSerial;
    private Double aircraftTotalHour;
    private Integer aircraftTotalCycle;
    private Double aCheckRemainHour;
    private Long aCheckRemainDays;
    private ItemColorCode acColorCode;
}
