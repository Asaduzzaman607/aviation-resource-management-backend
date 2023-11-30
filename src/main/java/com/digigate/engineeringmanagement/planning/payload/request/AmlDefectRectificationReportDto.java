package com.digigate.engineeringmanagement.planning.payload.request;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AmlDefectRectificationReportDto {
    private Long aircraftId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long airportId;
    private String rectDescription;
    private String defDescription;
    private String position;
    private String rectPnOff;
    private String rectSnOff;
    private String rectPnOn;
    private String rectSnOn;
    private String rectAta;
    private String reasonForRemoval;
    private String remark;
    private String woNo;
    private Boolean isPageable = false;
    private Boolean isDecimal = false;
}
