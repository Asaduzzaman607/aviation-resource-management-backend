package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Landing Gear Report View Model
 *
 * @author Nafiul Islam
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LandingGearReportViewModel {
    private List<LandingGearViewModel> landingGearViewModelList;
    private LocalDate asOnDate;
    private LocalDate domDate;
    private Double tat;
    private Integer tac;
    private Integer csn;
    private Double tsn;
    private Double tso;
    private Integer cso;
    private Double averageUtilizationHrsOrDay;
    private Integer averageCycIrDay;
}
