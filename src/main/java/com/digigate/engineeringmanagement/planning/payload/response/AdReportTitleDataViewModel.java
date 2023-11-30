package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * AdReportTitleData ViewModel
 *
 * @author Ashraful
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdReportTitleDataViewModel {
    private Integer totalAirframeCycle;
    private Double totalAirframeTime;
    private String aircraftRegn;
    private String manufacturerSerialNo;
    private LocalDate updatedTime;
}
