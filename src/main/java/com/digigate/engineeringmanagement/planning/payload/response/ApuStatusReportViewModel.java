package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Apu Status Report View Model
 *
 * @author Nafiul Islam
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApuStatusReportViewModel {

    private List<ApuStatusReportModel> apuStatusReportModel;
    private String acType;
    private String acRegn;
    private String acMsn;
    private LocalDate date;
    private Double tat;
    private Integer tac;
    private Double averageHours;
    private Integer averageCycle;
    private ApuShopVisitInfo apuShopVisitInfo;
    private ApuInfo apuInfo;

}
