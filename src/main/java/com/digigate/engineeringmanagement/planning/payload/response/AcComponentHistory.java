package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class AcComponentHistory {
    private String acRegNo;

    private String position;

    private String inRefMessage;
    private String outRefMessage;

    private LocalDate inDate;
    private LocalDate outDate;

    private Double timeNewHour;
    private Integer timeNewCycle;

    private Double timeOverHaulHour;
    private Integer timeOverHaulCycle;

    private Double aircraftInHour;
    private Integer aircraftInCycle;

    private Double aircraftOutHour;
    private Integer aircraftOutCycle;

    private String reasonForRemoval;

    private String higherSerialNo;

    public AcComponentHistory(String acRegNo, String position, String inRefMessage, String outRefMessage, LocalDate inDate,
                              LocalDate outDate, Double timeNewHour, Integer timeNewCycle, Double timeOverHaulHour,
                              Integer timeOverHaulCycle, Double aircraftInHour, Integer aircraftInCycle, Double aircraftOutHour,
                              Integer aircraftOutCycle, String reasonForRemoval, String higherSerialNo) {
        this.acRegNo = acRegNo;
        this.position = position;
        this.inRefMessage = inRefMessage;
        this.outRefMessage = outRefMessage;
        this.inDate = inDate;
        this.outDate = outDate;
        this.timeNewHour = timeNewHour;
        this.timeNewCycle = timeNewCycle;
        this.timeOverHaulHour = timeOverHaulHour;
        this.timeOverHaulCycle = timeOverHaulCycle;
        this.aircraftInHour = aircraftInHour;
        this.aircraftInCycle = aircraftInCycle;
        this.aircraftOutHour = aircraftOutHour;
        this.aircraftOutCycle = aircraftOutCycle;
        this.reasonForRemoval = reasonForRemoval;
        this.higherSerialNo = higherSerialNo;
    }

    private Double usedHour;
    private Integer usedCycle;

    private Double fittedTsn;
    private Integer fittedCsn;

    private Double fittedTso;
    private Integer fittedCso;





}
