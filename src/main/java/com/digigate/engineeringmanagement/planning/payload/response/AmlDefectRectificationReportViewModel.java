package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AmlDefectRectificationReportViewModel {
    private LocalDate amlDate;
    private LocalDate nrcDate;
    private String defectAirportIataCode;

    private Double amlAirFrameTotalTime;
    private Integer amlAirframeTotalCycle;

    private Integer pageNo;
    private Character alphabet;
    private String nrcNo;

    private String defectDescription;
    private String rectDescription;

    private String rectAta;
    private String rectPos;


    private String rectPnOff;
    private String rectSnOff;
    private String rectPnOn;
    private String rectSnOn;

    private String rectGrn;

    private String reasonForRemoval;
    private String remark;
    private String woNo;
}
